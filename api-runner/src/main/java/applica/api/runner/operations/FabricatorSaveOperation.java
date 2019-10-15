package applica.api.runner.operations;

import applica.api.domain.model.geo.GeoCity;
import applica.api.domain.model.users.Fabricator;
import applica.api.runner.facade.AccountFacade;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseSaveOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class FabricatorSaveOperation extends BaseSaveOperation {

    @Autowired
    private AccountFacade accountFacade;

    @Override
    public Class<? extends Entity> getEntityType() {
        return Fabricator.class;
    }

    @Override
    protected void finishEntity(ObjectNode node, Entity entity) {

        Fabricator fabricator = ((Fabricator) entity);

        fabricator.getAddress().setPhoneNumber(node.get("_phoneNumber") != null ? node.get("_phoneNumber").asText() : null);

        if (node.get("_address") != null && !node.get("_address").isNull())
            fabricator.getAddress().setAddress(StringUtils.hasLength(node.get("_address").asText()) ? node.get("_address").asText() : null);
        if (node.get("_streetNumber") != null && !node.get("_streetNumber").isNull())
            fabricator.getAddress().setStreetNumber(StringUtils.hasLength(node.get("_streetNumber").asText()) ? node.get("_streetNumber").asText() : null);

        if (node.get("_city") != null && !node.get("_city").isNull()){

            if (node.get("_city").isObject()){
                fabricator.getAddress().setCity(Repo.of(GeoCity.class).get(node.get("_city").get("id").asText()).orElse(null));
            }
        }

        if (node.get("_category") != null){
            fabricator.setCategoryId(node.get("_category").get("id").asText());
        }
    }

//    @Override
//    protected void beforeSave(ObjectNode data, Entity entity) {
//        String passwordToSave = null;
//        if (org.springframework.util.StringUtils.hasLength(data.get("password").asText())) {
//            //set / modifica password
//            passwordToSave = new BCryptPasswordEncoder().encode(data.get("password").asText());
//            ((User) entity).setCurrentPasswordSetDate(new Date());
//        } else {
//            if (entity.getId() != null) {
//                User previous = Repo.of(User.class).get(((User) entity).getSid()).get();
//                passwordToSave = ((User) previous).getPassword();
//            }
//        }
//        ((User) entity).setPassword(passwordToSave);
//        ((User) entity).setMail(((User) entity).getMail());
//
//    }
//
//    @Override
//    protected void afterSave(ObjectNode node, Entity entity) {
//        // Ottengo tutte le info necessarie ad aggiornare/creare un utente
//        User user = (User) entity;
//
//        if (user.isActive()) {
//            boolean needToActivate = false;
//
//            // Se nuovo utente autogenero password temporanea
//            if (node.get("id") == null) {
//                needToActivate = true;
//                user.setFirstLogin(true);
//                user.setRegistrationDate(new Date());
//
//                String tempPassword = user.getSid();
//                user.setPassword(new BCryptPasswordEncoder().encode(tempPassword));
//            }
//
//            Repo.of(User.class).save(user);
//
//            if (needToActivate) {
//                new Thread(() -> accountFacade.sendRegistrationMail(user, user.getSid())).start();
//            }
//        }
//
//    }
}
