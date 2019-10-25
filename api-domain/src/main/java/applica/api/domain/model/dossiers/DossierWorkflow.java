package applica.api.domain.model.dossiers;

import applica.api.domain.exceptions.WorkflowException;
import applica.api.domain.model.users.Customer;
import applica.api.domain.model.users.Fabricator;
import applica.api.domain.model.users.Person;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

public class DossierWorkflow {

    private Dossier dossier;

    public DossierWorkflow() {

    }

    public DossierWorkflow(Dossier dossier) {
        this.dossier = dossier;
    }

    public void create(Person fabricator, Person customer, PriceCalculatorSheet priceCalculatorSheet, String notes, boolean serviceFeeInvoiced) {
        var dossier = new Dossier();

        dossier.setCreationDate(new Date());
        dossier.setFabricatorId(fabricator.getId());
        dossier.setCustomerId(customer.getId());
        dossier.setStatus(Dossier.STATUS_QUOTATION);
        dossier.setPriceCalculatorSheet(priceCalculatorSheet);
        dossier.setNotes(notes);
        dossier.setServiceFeeInvoiced(serviceFeeInvoiced);

        this.dossier = dossier;
    }

    public void edit(Fabricator fabricator, Customer customer, PriceCalculatorSheet priceCalculatorSheet, String notes, boolean serviceFeeInvoiced) {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

        dossier.setFabricatorId(fabricator.getId());
        dossier.setCustomerId(customer.getId());
        dossier.setPriceCalculatorSheet(priceCalculatorSheet);
        dossier.setNotes(notes);
        dossier.setServiceFeeInvoiced(serviceFeeInvoiced);

    }

    public Dossier get(){
        return this.dossier;
    }

    public void quotation() throws WorkflowException {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

//        if (!dossier.getStatus().equals(Dossier.STATUS_PAY_OFF)) {
            dossier.setStatus(Dossier.STATUS_QUOTATION);
//        } else {
//            throw new WorkflowException();
//        }
    }

    /**
     *
     */
    public void confirmQuotation() throws WorkflowException {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

//        if (dossier.getStatus().equals(Dossier.STATUS_QUOTATION) || dossier.getStatus().equals(Dossier.STATUS_TO_CANDIDATE)) {
            dossier.setStatus(Dossier.STATUS_DRAFT);
//        } else {
//            throw new WorkflowException();
//        }
    }

    /**
     * Try to move a dossier from draft status to toVerify status
     */
    public void commit() throws WorkflowException {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

//        if (dossier.getStatus().equals(Dossier.STATUS_DRAFT)) {
            dossier.setStatus(Dossier.STATUS_TO_CANDIDATE);
//        } else {
//            throw new WorkflowException();
//        }
    }

    public void candidate() throws WorkflowException {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

//        if (dossier.getStatus().equals(Dossier.STATUS_TO_CANDIDATE)) {
            dossier.setStatus(Dossier.STATUS_CANDIDATED);
//        } else {
//            throw new WorkflowException();
//        }
    }

    public void approve() throws WorkflowException {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

//        if (dossier.getStatus().equals(Dossier.STATUS_CANDIDATED)) {
            dossier.setStatus(Dossier.STATUS_APPROVED);
//        } else {
//            throw new WorkflowException();
//        }
    }

    public void refuse() throws WorkflowException {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

//        if (dossier.getStatus().equals(Dossier.STATUS_CANDIDATED)) {
            dossier.setStatus(Dossier.STATUS_REFUSED);
//        } else {
//            throw new WorkflowException();
//        }
    }

    public void payOff() throws WorkflowException {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

//        if (dossier.getStatus().equals(Dossier.STATUS_APPROVED)) {
            dossier.setStatus(Dossier.STATUS_PAY_OFF);
//        } else {
//            throw new WorkflowException();
//        }
    }

    public void attachDocument(Object documentTypeId, String file, String preview) {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

        dossier.getDocuments().stream().forEach(document -> {
            if (Objects.equals(documentTypeId, document.getDocumentTypeId())){
                document.setFile(file);
                document.setValid(true);
                document.setUploadDate(new Date());
                document.setStatus(Document.UPLOADED);
                document.setPreview(preview);
                document.setRefuseReason(null);
                document.setRefusedDate(null);
            }
        });

        if (Objects.equals(dossier.getStatus(), Dossier.STATUS_DRAFT) && dossier.getDocuments().stream().filter(d->Objects.equals(d.getDocumentType().getAssignationType(), DocumentType.PREPARATORY_DOCUMENTATION) && d.getFile() == null || (d.getFile() != null && !d.isValid())).collect(Collectors.toList()).size() == 0) {
            try {
                commit();
            } catch (WorkflowException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearDocumentAttachment(Object documentTypeId) {
        dossier.getDocuments().forEach(document -> {
            if (document.getDocumentTypeId().equals(documentTypeId)) {
                document.setFile(null);
                document.setPreview(null);
                document.setUploadDate(null);
                document.setStatus(Document.TO_BE_UPLOAD);
            }
        });
    }

    public void refuseDocument(Object documentTypeId, String refuseReason) {
        dossier.getDocuments().forEach(document -> {
            if (document.getDocumentTypeId().equals(documentTypeId)) {
                document.setValid(false);
                document.setRefusedDate(new Date());
                document.setRefuseReason(refuseReason);
                document.setStatus(Document.TO_RECHARGE);
            }
        });
    }
}
