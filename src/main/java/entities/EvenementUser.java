package entities;

import javax.persistence.*;

@Entity
@Table(name = "evenement_user")
public class EvenementUser {


    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EvenementUser(Evenement evenement, User user) {
        this.evenement = evenement;
        this.user = user;
    }

    public EvenementUser() {

    }
}
