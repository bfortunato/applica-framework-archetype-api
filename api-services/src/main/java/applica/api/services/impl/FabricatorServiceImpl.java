package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.User;
import applica.api.domain.model.dossiers.DocumentType;
import applica.api.domain.model.dossiers.Dossier;
import applica.api.domain.model.users.Fabricator;
import applica.api.domain.utils.CustomLocalizationUtils;
import applica.api.services.DossiersService;
import applica.api.services.FabricatorService;
import applica.api.services.MailService;
import applica.api.services.responses.ResponseCode;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.Result;
import applica.framework.library.i18n.LocalizationUtils;
import applica.framework.library.mail.Recipient;
import applica.framework.library.mail.TemplatedMail;
import applica.framework.library.options.OptionsManager;
import applica.framework.notifications.MessagingException;
import applica.framework.notifications.Notification;
import applica.framework.notifications.NotificationService;
import applica.framework.security.Security;
import applica.framework.widgets.operations.OperationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
public class FabricatorServiceImpl implements FabricatorService {

    @Autowired
    private OptionsManager options;

    @Autowired
    private MailService mailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DossiersService dossiersService;

    @Override
    public Result<Fabricator> findAllActive() {
        return Repo.of(Fabricator.class).find(Query.build().eq(Filters.ACTIVE, true));
    }

    @Override
    public Result<Fabricator> findActiveByQuery(Query query) {
        if (query == null)
            query = Query.build();
        if (StringUtils.isNotEmpty(query.getKeyword())) {
            query.getFilters().add(new Filter(Filters.BUSINESS_NAME, query.getKeyword(), Filter.LIKE));
        }
        query.getFilters().add(new Filter(Filters.ACTIVE, true, Filter.EQ));
        return Repo.of(Fabricator.class).find(query);
    }

    @Override
    public void materializeUser(Fabricator fabricator) {
        fabricator.setUser(Repo.of(User.class).get(fabricator.getUserId()).orElse(null));
    }

    @Override
    public Object getLoggedUserFabricatorId() throws OperationException {
        User user = (User) Security.withMe().getLoggedUser();
        Fabricator fabricator = Repo.of(Fabricator.class).find(Query.build().eq(Filters.USER_ID, user.getSid())).findFirst().orElseThrow(()-> new OperationException(ResponseCode.ERROR_FABRICATOR_NOT_FOUND));
        return fabricator.getId();
    }

    @Override
    public void sendDocumentRefusedMail(Dossier dossier, DocumentType documentType) {
        dossiersService.materializeFabricator(dossier);
        materializeUser(dossier.getFabricator());

        TemplatedMail templatedMail = new TemplatedMail();
        templatedMail.setOptions(options);
        templatedMail.setMailFormat(TemplatedMail.HTML);
        templatedMail.setTemplatePath("mailTemplates/documentRefused.vm");
        templatedMail.setFrom(options.get("registration.mail.from"));
        templatedMail.setSubject(String.format(CustomLocalizationUtils.getInstance().getMessage("notifications.refuse.document.title"), dossier.getCode()));
        templatedMail.setTo(dossier.getFabricator().getUser().getMail());
        templatedMail.put("businessName", dossier.getFabricator().getBusinessName());
        templatedMail.put("documentType", documentType.getDescription());

        mailService.sendMail(templatedMail, Collections.singletonList(new Recipient(dossier.getFabricator().getUser().getMail(), Recipient.TYPE_TO)));
    }

    private Notification generateDossierChangeStatusNotification(Dossier dossier, Object senderId) {
        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setDate(new Date());
        notification.setTitle(String.format(LocalizationUtils.getInstance().getMessage("notifications.change.status.title"), dossier.getCode()));
        notification.setBody(String.format(LocalizationUtils.getInstance().getMessage("notifications.change.status.body"), dossier.getStatus()));
        return notification;
    }

    public void sendDossierStatusChangedNotification(Dossier dossier, Object senderId) {
        new Thread(()-> {
            dossiersService.materializeFabricator(dossier);
            try {
                notificationService.notify(generateDossierChangeStatusNotification(dossier, senderId), dossier.getFabricator().getUserId());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Notification generateDossierRefuseDocumentNotification(Dossier dossier, Object senderId) {
        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setDate(new Date());
        notification.setTitle(String.format(LocalizationUtils.getInstance().getMessage("notifications.refuse.document.title"), dossier.getCode()));
        notification.setBody(String.format(LocalizationUtils.getInstance().getMessage("notifications.refuse.document.body"), dossier.getStatus()));
        return notification;
    }

    public void sendDossierDocumentRefusedNotification(Dossier dossier, Object senderId) {
        new Thread(()-> {
            dossiersService.materializeFabricator(dossier);
            try {
                notificationService.notify(generateDossierRefuseDocumentNotification(dossier, senderId), dossier.getFabricator().getUserId());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
