package entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AbonnementAvecEngagement extends Abonnement {
    private Integer dureeEngagementMois;

    public AbonnementAvecEngagement() {
        super();
    }

    public AbonnementAvecEngagement(String nomService, BigDecimal montantMensuel,
                                    LocalDate dateDebut, Integer dureeEngagementMois) {
        super(nomService, montantMensuel, dateDebut);
        this.dureeEngagementMois = dureeEngagementMois;
        this.dateFin = dateDebut.plusMonths(dureeEngagementMois);
    }

    @Override
    public String getTypeAbonnement() {
        return "AVEC_ENGAGEMENT";
    }

    public Integer getDureeEngagementMois() { return dureeEngagementMois; }
    public void setDureeEngagementMois(Integer dureeEngagementMois) {
        this.dureeEngagementMois = dureeEngagementMois;
    }
}