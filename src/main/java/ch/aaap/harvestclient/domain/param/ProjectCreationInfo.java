package ch.aaap.harvestclient.domain.param;

import ch.aaap.harvestclient.domain.Project;
import ch.aaap.harvestclient.domain.reference.ClientReference;
import ch.aaap.harvestclient.domain.reference.dto.ClientReferenceDto;

public class ProjectCreationInfo extends Project {

    /**
     * Takes the place of clientReference
     */
    private Long clientId;

    public ProjectCreationInfo(ClientReference clientReference, String name, boolean isBillable, BillingMethod billBy,
            BudgetMethod budgetBy) {
        super();
        clientId = clientReference.getId();
        setName(name);
        setBillable(isBillable);
        setBillBy(billBy);
        setBudgetBy(budgetBy);
    }

    public void setClientReference(ClientReference clientReference) {
        clientId = clientReference.getId();
    }

    @Override
    public void setClientReference(ClientReferenceDto clientReference) {
        clientId = clientReference.getId();
    }
}
