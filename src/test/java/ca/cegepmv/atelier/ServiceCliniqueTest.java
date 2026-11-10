package ca.cegepmv.atelier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Atelier — Réusinage (refactoring)
 * =================================
 *
 * Cette suite de tests est VOLONTAIREMENT INCOMPLÈTE. Les 4 tests ci-dessous passent
 * déjà et couvrent seulement une partie du comportement de {@link ServiceClinique}.
 *
 * Avant de réusiner quoi que ce soit (voir README, Partie 3), complétez cette classe
 * (Partie 2) avec des tests de CARACTÉRISATION qui documentent le comportement ACTUEL
 * du code (pas celui que vous jugeriez "idéal") pour :
 *
 *   1. genererFacture() — le cas VACCIN, le cas où le rabais de fidélité s'applique
 *      (2 animaux ou plus), et le cas de pénalité de solde négatif.
 *   2. calculerRemiseFidelite() — les 3 branches non couvertes (0.15, 0.10, 0.05).
 *   3. estimerCoutAnnuel() — au moins un cas avec plusieurs visites de types différents.
 *   4. calculerDureeRendezVous() — au moins les 4 types (CONSULTATION, VACCIN,
 *      CHIRURGIE, type inconnu).
 *   5. genererRapportPourVeterinaire() — au moins un cas normal.
 *   6. planifierVisite() — le cas avec rappel par courriel activé ET désactivé
 *      (vérifiez le contenu de getJournalRappels()).
 *
 * N'ajoutez PAS de test pour un comportement que vous jugez incorrect (ex. le tarif
 * silencieux à 0$ pour un type inconnu) sans d'abord relire l'avertissement dans
 * ServiceClinique — ce comportement est un piège intentionnel de l'atelier.
 */
class ServiceCliniqueTest {

    private ServiceClinique service;
    private Proprietaire julie;
    private Proprietaire marco;

    @BeforeEach
    void setUp() {
        service = new ServiceClinique();
        // Arrange commun : propriétaire avec un seul animal, solde à jour, cliente depuis 1 an
        julie = new Proprietaire("Julie", "Tremblay", "123 rue des Lilas", "Longueuil",
                "J4G 1A1", "julie.tremblay@example.com", "450-555-0100", 0.0, 1);
        julie.ajouterAnimal(new Animal(1, "Rex", "CHIEN", LocalDate.of(2020, 3, 15)));
    }

    // ------------------------------------------------------------------
    // Tests fournis (déjà complets, à titre d'exemple — NE PAS MODIFIER)
    // ------------------------------------------------------------------

    @Test
    void genererFacture_uneConsultationSansUrgence_calculeLeBonSousTotal() {
        // Arrange
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", "Visite de routine");

        // Act
        String facture = service.genererFacture(julie, List.of(consultation));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $60.00 — Total: $60.00", facture);
    }

    @Test
    void genererFacture_chirurgieUrgente_appliqueLeMultiplicateurDurgence() {
        // Arrange
        Visite chirurgieUrgente = new Visite(LocalDate.now(), "CHIRURGIE", true, "Dr Gagnon", "Urgence");

        // Act
        String facture = service.genererFacture(julie, List.of(chirurgieUrgente));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $375.00 — Total: $375.00", facture);
    }

    @Test
    void genererFacture_sansVisite_lanceUneException() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.genererFacture(julie, List.of()));
    }

    @Test
    void calculerRemiseFidelite_unSeulAnimalEtPeuAnciennete_retourneZero() {
        // Act
        double remise = service.calculerRemiseFidelite(julie);

        // Assert
        assertEquals(0.0, remise);
    }

    // ------------------------------------------------------------------
    // À COMPLÉTER — voir README, Partie 2 (tests de caractérisation)
    // ------------------------------------------------------------------

    // TODO 1 — genererFacture() : cas VACCIN (tarif de base 35$, sans urgence)
    @Test
    void genererFacture_vaccinSansUrgence(){
        // Arrange
        Visite consultation = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Visite de routine");

        // Act
        String facture = service.genererFacture(julie, List.of(consultation));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $35.00 — Total: $35.00", facture);
    }

    // TODO 2 — genererFacture() : propriétaire avec 2 animaux ou plus (rabais de 10%
    //          appliqué, et présent dans le texte de la facture — vérifiez le format
    //          exact affiché par le code actuel avant d'écrire votre assertion)
    @Test
    void genererFacture_deuxAnimeaux_verifierFormatEtRabais(){
        // Arrange
        julie.ajouterAnimal(new Animal(2, "SAM", "CHIEN", LocalDate.of(2020, 3, 15)));
        Visite consultation = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Visite de routine");

        // Act
        String facture = service.genererFacture(julie, List.of(consultation));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $35.00 — Remise fidélité: -10% — Total: $31.50", facture);
    }

    // TODO 3 — genererFacture() : propriétaire avec un solde négatif (pénalité de 5%)
    @Test
    void genererFacture_proprietaires(){
        // Arrange
        Proprietaire marco = new Proprietaire("Marco", "Tremblay", "125 rue des Lilas", "Longueuil",
        "J4G 1A1", "marco.tremblay@example.com", "450-556-0127", -10.00, 2);
        marco.ajouterAnimal(new Animal(2, "Sam", "CHIEN", LocalDate.of(2020, 3, 15)));
        Visite consultation = new Visite(LocalDate.now(), "VACCIN", false, "Dr Gagnon", "Visite de routine");

        // Act
        String facture = service.genererFacture(marco, List.of(consultation));

        // Assert
        assertEquals("Facture pour Marco Tremblay — Sous-total: $35.00 — Total: $36.75", facture);
    }

    // TODO 4 — calculerRemiseFidelite() : plus d'un animal ET anneesClient >= 3 (0.15)
    @Test
    void calculerRemiseFidelite_deuxAnimeaux_plus3AnsClient() {
        // Act
        Proprietaire mathieu = new Proprietaire("Mathieu", "Tremblay", "125 rue des Lilas", "Longueuil",
        "J4G 1A1", "mathieu.tremblay@example.com", "450-556-0127", 0.00, 10);
        mathieu.ajouterAnimal(new Animal(1, "Sam", "CHIEN", LocalDate.of(2020, 3, 15)));
        mathieu.ajouterAnimal(new Animal(2, "Carth", "CHAT", LocalDate.of(2020, 3, 15)));
        double remise = service.calculerRemiseFidelite(mathieu);

        // Assert
        assertEquals(0.15, remise);
    }

    // TODO 5 — calculerRemiseFidelite() : plus d'un animal ET anneesClient < 3 (0.10)
    @Test
    void calculerRemiseFidelite_deuxAnimeaux_2AnsClient() {
        // Act
        Proprietaire mathieu = new Proprietaire("Mathieu", "Tremblay", "125 rue des Lilas", "Longueuil",
        "J4G 1A1", "mathieu.tremblay@example.com", "450-556-0127", 0.00, 2);
        mathieu.ajouterAnimal(new Animal(1, "Sam", "CHIEN", LocalDate.of(2020, 3, 15)));
        mathieu.ajouterAnimal(new Animal(2, "Carth", "CHAT", LocalDate.of(2020, 3, 15)));
        double remise = service.calculerRemiseFidelite(mathieu);

        // Assert
        assertEquals(0.1, remise);
    }

    
    // TODO 6 — calculerRemiseFidelite() : un seul animal ET anneesClient >= 5 (0.05)
    @Test
    void calculerRemiseFidelite_unSeulAnimal_5AnsClient() {
        // Arrange
        Proprietaire mathieu = new Proprietaire("Mathieu", "Tremblay",
                "125 rue des Lilas", "Longueuil", "J4G 1A1",
                "mathieu.tremblay@example.com", "450-556-0127", 0.00, 5);

        mathieu.ajouterAnimal(new Animal(1, "Sam", "CHIEN",
                LocalDate.of(2020, 3, 15)));

        // Act
        double remise = service.calculerRemiseFidelite(mathieu);

        // Assert
        assertEquals(0.05, remise);
    }

    // TODO 7 — estimerCoutAnnuel() : plusieurs visites de types différents
    @Test
    void estimerCoutAnnuel_plusieursTypesVisites_retourneCoutAnnuel() {
        // Arrange
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION",
                false, "Dr Gagnon", "Visite de routine");

        Visite vaccin = new Visite(LocalDate.now(), "VACCIN",
                false, "Dr Gagnon", "Vaccination");

        Visite chirurgie = new Visite(LocalDate.now(), "CHIRURGIE",
                false, "Dr Gagnon", "Chirurgie");

        // Act
        double coutAnnuel = service.estimerCoutAnnuel(
                List.of(consultation, vaccin, chirurgie)
        );

        // Assert
        // (60 + 35 + 250) * 12 = 4140
        assertEquals(4140.0, coutAnnuel);
    }

    // TODO 8 — calculerDureeRendezVous() : les 4 types de visites
    @Test
    void calculerDureeRendezVous_typesVisites_retourneBonnesDurees() {
        // Arrange
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION",
                false, "Dr Gagnon", "Routine");

        Visite vaccin = new Visite(LocalDate.now(), "VACCIN",
                false, "Dr Gagnon", "Vaccination");

        Visite chirurgie = new Visite(LocalDate.now(), "CHIRURGIE",
                false, "Dr Gagnon", "Chirurgie");

        Visite typeInconnu = new Visite(LocalDate.now(), "AUTRE",
                false, "Dr Gagnon", "Autre visite");

        // Act + Assert
        assertAll(
                () -> assertEquals(20, service.calculerDureeRendezVous(consultation)),
                () -> assertEquals(10, service.calculerDureeRendezVous(vaccin)),
                () -> assertEquals(90, service.calculerDureeRendezVous(chirurgie)),
                () -> assertEquals(30, service.calculerDureeRendezVous(typeInconnu))
        );
    }

    // TODO 9 — genererRapportPourVeterinaire() : un cas normal
    @Test
    void genererRapportPourVeterinaire_casNormal_genereRapportComplet() {
        // Arrange
        Veterinaire veterinaire = new Veterinaire("Gagnon", "Chirurgie");

        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION",
                false, "Dr Gagnon", "Routine");

        Visite vaccin = new Visite(LocalDate.now(), "VACCIN",
                false, "Dr Gagnon", "Vaccination");

        // Act
        String rapport = service.genererRapportPourVeterinaire(
                veterinaire, julie, List.of(consultation, vaccin)
        );

        // Assert
        String rapportAttendu =
                "Rapport du Dr Gagnon (Chirurgie)\n" +
                "Client: Julie Tremblay\n" +
                "Adresse: 123 rue des Lilas, Longueuil J4G 1A1\n" +
                "Contact: julie.tremblay@example.com / 450-555-0100\n" +
                "Nombre de visites: 2";

        assertEquals(rapportAttendu, rapport);
    }

    // TODO 10 — planifierVisite() : rappel par courriel activé
    @Test
    void planifierVisite_rappelActive_ajouteRappelAuJournal() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 11, 15);

        // Act
        Visite visite = service.planifierVisite(
                1, date, "CONSULTATION", false,
                "Dr Gagnon", "Visite de routine", true
        );

        // Assert
        assertNotNull(visite);

        assertEquals(
                List.of("Rappel envoyé pour la visite du 2026-11-15 (animal #1)"),
                service.getJournalRappels()
        );
    }

    // TODO 10 — planifierVisite() : rappel par courriel désactivé
    @Test
    void planifierVisite_rappelDesactive_journalResteVide() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 11, 15);

        // Act
        Visite visite = service.planifierVisite(
                1, date, "CONSULTATION", false,
                "Dr Gagnon", "Visite de routine", false
        );

        // Assert
        assertNotNull(visite);
        assertTrue(service.getJournalRappels().isEmpty());
    }

}