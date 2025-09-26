package entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AbonnementSansEngagement extends Abonnement {

    public AbonnementSansEngagement() {
        super();
    }

    public AbonnementSansEngagement(String nomService, BigDecimal montantMensuel, LocalDate dateDebut) {
        super(nomService, montantMensuel, dateDebut);
    }

    @Override
    public String getTypeAbonnement() {
        return "SANS_ENGAGEMENT";
    }
}