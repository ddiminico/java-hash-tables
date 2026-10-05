package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe gestora que fa de pont entre la nostra aplicació i les diferents
 * implementacions de taules de hashing.
 */
public class HashingCancons {

    // Aquesta variable guardarà la nostra taula.
    // L'ús de la interfície 'IHashMap' ens permet aplicar polimorfisme:
    // podem guardar aquí qualsevol de les 3 versions de la taula sense canviar el tipus de la variable.
    // La Clau (K) serà l'String del títol i el Valor (V) serà l'objecte Canco.
    private IHashMap<String, Canco> taula;

    /**
     * Constructor que crea una estructura buida
     * @param tipus 1 = No Encadenada, 2 = Indirecte (Encadenada), 3 = HashMap natiu de Java
     * @param mida La capacitat inicial de la taula de hashing
     */
    public HashingCancons(int mida, int tipus) {
        inicialitzarTaula(tipus, mida);
    }

    /**
     * Constructor que crea l'estructura i de seguida carrega les dades des d'un fitxer CSV
     */
    public HashingCancons(int mida, String fitxer, int tipus) {
        inicialitzarTaula(tipus, mida);
        carregarDades(fitxer);
    }

    // --- Mètodes auxiliars (Privats, ja que només s'usen internament) ---

    /**
     * Mètode per triar el "motor" (la implementació) de la nostra taula de dades.
     * Funciona com un patró de disseny "Simple Factory".
     */
    private void inicialitzarTaula(int tipus, int mida) {
        if (mida < 1) {
            throw new IllegalArgumentException("La mida ha de ser positiva");
        }

        switch (tipus) {
            case 3:
                taula = new HashingHashmap<>(mida);
                break;
            case 2:
                taula = new HashingIndirecte<>(mida);
                break;
            case 1:
                taula = new HashingNoEncadenat<>(mida);
                break;
            default:
                throw new IllegalArgumentException("Error: tipus d'implementació incorrecte. Tria 1, 2 o 3.");
        }
    }

    /**
     * Mètode per llegir el fitxer .csv línia a línia i crear els objectes Canco.
     */
    private void carregarDades(String fitxer) {
        java.io.File fitx = new java.io.File(fitxer);
        if (!fitx.exists()) {
            throw new IllegalArgumentException("El fitxer no existeix");
        }

        // Utilitzem un "try-with-resources" (el BufferedReader dins dels parèntesis del try).
        // Això garanteix que el fitxer es tancarà automàticament al finalitzar, hi hagi errors o no,
        // evitant fuites de memòria (memory leaks).
        try (BufferedReader f = new BufferedReader(new FileReader(fitxer))) {
            String linia, titol, artista, genere;
            String[] dades;
            int id, any, durada, popularitat;

            linia = f.readLine();   // Llegim la primera línia (capçalera) i la ignorem perquè no conté dades reals.

            // Bucle que llegeix línia a línia fins que readLine() retorna null (final del fitxer)
            while ((linia = f.readLine()) != null) {
                try {
                    // Tallem l'String per les comes. 'split' crea un Array d'Strings amb cada fragment.
                    dades = linia.split(",");
                    if (dades.length < 7) {
                        throw new IllegalArgumentException("Fitxer malformat"); // Si falten columnes, descartem la línia.
                    }

                        // Parsejem (convertim) els Strings a enters on calgui.
                        // El '.trim()' elimina possibles espais en blanc al principi o final per evitar errors de conversió.
                        id = Integer.parseInt(dades[0].trim());
                        titol = dades[1].trim();
                        artista = dades[2].trim();
                        genere = dades[3].trim();
                        any = Integer.parseInt(dades[4].trim());
                        durada = Integer.parseInt(dades[5].trim());
                        popularitat = Integer.parseInt(dades[6].trim());

                        // Instanciem l'objecte amb les dades netes
                        Canco c = new Canco(id, titol, artista, genere, any, durada, popularitat);

                        // La inserim a la taula abstracta.
                        // Segons com s'hagi inicialitzat, executarà la lògica d'inserció d'una classe o d'una altra.
                        taula.inserir(titol, c);

                } catch (NumberFormatException e) {
                    // Si al fer 'Integer.parseInt' hi ha lletres en comptes de números, es captura l'error aquí.
                    throw new IllegalArgumentException("Dada numèrica incorrecta al CSV");
                }
            }
        } catch (IOException | NumberFormatException e) {
            // Captura problemes d'accés al fitxer (disc dur ple, fitxer corrupte, etc.)
            throw new IllegalArgumentException("Error en llegir el fitxer");
        }
    }

    // --- Mètodes bàsics de la taula (Delegació pura) ---
    // Aquests mètodes simplement passen la petició directament a l'objecte 'taula'.

    public void inserir(Canco c) {
        taula.inserir(c.getTitol(), c);
    }

    public Canco consultar(String titol) throws ElementNoTrobat {
        return taula.consultar(titol);
    }

    public void esborrar(String titol) throws ElementNoTrobat {
        taula.esborrar(titol);
    }

    public boolean buscar(String titol) {
        return taula.buscar(titol);
    }

    // Sobrecàrrega de mètodes (Method Overloading): tenim dos mètodes 'buscar', un rep String i l'altre Canco.
    public boolean buscar(Canco c) {
        return taula.buscar(c.getTitol());
    }

    public boolean esBuida() {
        return taula.esBuida();
    }

    public int numElements() {
        return taula.numElements();
    }

    public int mida() {
        return taula.midaTaula();
    }

    public float factorCarrega() {
        return taula.factorCarrega();
    }

    /**
     * Converteix el contingut de la taula en un Array tradicional.
     */
    public Canco[] elements() {
        List<Canco> llista = new ArrayList<>();
        // El bucle 'for-each' funciona directament sobre 'taula' perquè
        // hem implementat un iterador a la interfície IHashMap.
        for (Canco c : taula) {
            llista.add(c);
        }
        // El truc 'new Canco[0]' indica a Java quin és el tipus d'Array que volem retornar.
        // Java s'encarrega de crear internament un Array de la mida correcta per encabir-ho tot.
        return llista.toArray(new Canco[0]);
    }

    // --- Mètodes avançats ---

    /**
     * Retorna un Array de Strings només amb els títols de les cançons, ordenats.
     * @return Array de títols.
     */
    public String[] obtenirTitols() {
        List<String> titols = new ArrayList<>();
        for (Canco c : taula) {
            titols.add(c.getTitol()); // Com l'iterador ja ens dona les dades ordenades, la llista es crea ordenada.
        }
        return titols.toArray(new String[0]);
    }

    /**
     * Exposa l'iterador de l'estructura subjacent.
     * @return un iterador de cançons ordenades alfabèticament pel títol.
     */
    public Iterator<Canco> recuperarElementsOrdenats() {
        return taula.iterator();
    }

    /**
     * Filtre simple. Recorre l'estructura i guarda només els elements que compleixen una condició.
     * @param minima el valor mínim de popularitat requerit.
     * @return Array amb les cançons filtrades.
     */
    public Canco[] canconsMesPopulars(int minima) {
        List<Canco> filtrades = new ArrayList<>();
        for (Canco c : taula) {
            if (c.getPopularitat() >= minima) {
                filtrades.add(c);
            }
        }
        return filtrades.toArray(new Canco[0]);
    }
}
