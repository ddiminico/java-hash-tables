package cat.urv.deim;

import java.util.Objects;

public class Canco implements Comparable<Canco> {
    private final int id;
    private final String titol;
    private final String artista;
    private final String genere;
    private final int any;
    private int duradaSegons;
    private int popularitat;

    public Canco(int id, String titol, String artista, String genere, int any, int duradaSegons, int popularitat) {
        this.id = id;
        this.titol = titol;
        this.artista = artista;
        this.genere = genere;
        this.any = any;
        this.duradaSegons = duradaSegons;
        this.popularitat = popularitat;
    }

    public int getId() {
        return id;
    }

    public String getTitol() {
        return titol;
    }

    public String getArtista() {
        return artista;
    }

    public String getGenere() {
        return genere;
    }

    public int getAny() {
        return any;
    }

    public int getDuradaSegons() {
        return duradaSegons;
    }

    public int getPopularitat() {
        return popularitat;
    }

    public void setDuradaSegons(int duradaSegons) {
        this.duradaSegons = duradaSegons;
    }

    public void setPopularitat(int popularitat) {
        this.popularitat = popularitat;
    }

    @Override
    public int compareTo(Canco altra) {
        // compareToIgnoreCase fa que minúscules i majúscules de la mateixa lletra es considerin iguals a l'hora d'ordenar
        return this.titol.compareToIgnoreCase(altra.getTitol());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;    // Si són exactament el mateix objecte a la memòria, són iguals
        }
        if (o == null || (getClass() != o.getClass())) {
            return false;   // Si l'altre és nul o no és una cançó, no són iguals
        }
        Canco canco = (Canco) o;
        // Dues cançons són la mateixa si tenen el mateix títol
        return Objects.equals(titol, canco.titol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titol);
    }

    @Override
    public String toString() {
        return "Canco{id=" + id +
                ", titol='" + titol + '\'' +
                ", artista='" + artista + '\'' +
                ", genere='" + genere + '\'' +
                ", any=" + any +
                ", duradaSegons=" + duradaSegons +
                ", popularitat=" + popularitat +
                '}';
    }
}
