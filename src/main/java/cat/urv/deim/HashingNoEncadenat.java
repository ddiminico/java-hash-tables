package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementació d'una taula de hash utilitzant ADREÇAMENT OBERT (Open Addressing).
 * Utilitza EXPLORACIÓ LINEAL: si la posició 'i' està ocupada, mirem la 'i+1', 'i+2', etc.
 */
public class HashingNoEncadenat<K, V> implements IHashMap<K, V> {

    /**
     * Entrada de la taula. A diferència del Node de la taula encadenada,
     * aquí NO hi ha punter al 'seguent', ja que tot es guarda a l'array principal.
     */
    private class Entrada {
        K clau;
        V valor;
        boolean esborrat; // PUNT D'ESTUDI: El "Tombstone". Indica si l'element s'ha eliminat lògicament.

        public Entrada(K clau, V valor) {
            this.clau = clau;
            this.valor = valor;
            this.esborrat = false;
        }
    }

    private ArrayList<Entrada> taula;
    private int midaTaula;
    private int numElements;    // Elements actius (no esborrats)
    private int numEsborrats;   // Comptador de "tombstones" per a gestió interna

    public HashingNoEncadenat(int mida) {
        this.midaTaula = mida;
        this.numElements = 0;
        this.numEsborrats = 0;
        this.taula = new ArrayList<>(mida);

        for (int i = 0; i < mida; i++) {
            this.taula.add(null);
        }
    }

    private int funcioHash(K clau) {
        return Math.abs(clau.hashCode() % midaTaula);
    }

    /**
     * Rehashing: Crea una taula nova i re-insereix només els elements vius.
     * PUNT D'ESTUDI: En redimensionar, els "tombstones" desapareixen, netejant la taula
     * i millorant la velocitat de cerca.
     */
    private void redimensionar() {
        int novaMida = this.midaTaula * 2;
        ArrayList<Entrada> novaTaula = new ArrayList<>(novaMida);

        for (int i = 0; i < novaMida; i++) {
            novaTaula.add(null);
        }

        ArrayList<Entrada> taulaAntiga = this.taula;
        this.taula = novaTaula;
        this.midaTaula = novaMida;
        this.numElements = 0;
        this.numEsborrats = 0; // Reiniciem comptadors

        for (Entrada e : taulaAntiga) {
            // Només passem a la nova taula els elements que no siguin nulls i que no estiguin esborrats
            if (e != null && !e.esborrat) {
                inserirSenseRedimensionar(e.clau, e.valor);
            }
        }
    }

    /**
     * Inserció simplificada per al procés de redimensionament.
     */
    private void inserirSenseRedimensionar(K key, V value) {
        int pos = funcioHash(key);
        // Exploració lineal: si està ocupat, passa al següent (circularment amb el mòdul %)
        while (taula.get(pos) != null) {
            pos = (pos + 1) % midaTaula;
        }
        taula.set(pos, new Entrada(key, value));
        numElements++;
    }

    @Override
    public void inserir(K key, V value) {
        // PAS 1: Cercar si la clau ja existeix per actualitzar el valor
        int pos = funcioHash(key);
        int elementsExplorats = 0;

        while (taula.get(pos) != null && elementsExplorats < midaTaula) {
            Entrada e = taula.get(pos);
            if (!e.esborrat && e.clau.equals(key)) {
                e.valor = value; // Trobada! Actualitzem i sortim.
                return;
            }
            pos = (pos + 1) % midaTaula;
            elementsExplorats++;
        }

        // PAS 2: Control del factor de càrrega (> 75%)
        // PUNT D'ESTUDI: Usar enters ((n+1)*100 >= 75*mida) és més precís que usar floats en sistemes crítics.
        if ((numElements + 1) * 100 >= 75 * midaTaula) {
            redimensionar();
        }

        // PAS 3: Inserció real cercant el primer lloc disponible (null o esborrat)
        pos = funcioHash(key);
        elementsExplorats = 0;
        int targetPos = -1; // Guardarem aquí la primera posició esborrada que trobem

        while (taula.get(pos) != null && elementsExplorats < midaTaula) {
            Entrada e = taula.get(pos);
            // Si trobem un "tombstone" i encara no n'havíem vist cap, guardem la posició per si ens cal
            if (e.esborrat && targetPos == -1) {
                targetPos = pos;
            }
            pos = (pos + 1) % midaTaula;
            elementsExplorats++;
        }

        // Si no hem trobat cap "tombstone", la posició d'inserció és el primer 'null' que ha aturat el while
        if (targetPos == -1) {
            targetPos = pos;
        }

        // Si estem ocupant el lloc d'un element esborrat, restem del comptador d'esborrats
        if (taula.get(targetPos) != null && taula.get(targetPos).esborrat) {
            numEsborrats--;
        }

        taula.set(targetPos, new Entrada(key, value));
        numElements++;
    }

    @Override
    public V consultar(K key) throws ElementNoTrobat {
        int pos = funcioHash(key);
        int elementsExplorats = 0;

        // PUNT D'ESTUDI: La cerca NO s'atura si troba un element 'esborrat'.
        // Només s'atura si troba un 'null' o si ha recorregut tota la taula.
        while (taula.get(pos) != null && elementsExplorats < midaTaula) {
            Entrada e = taula.get(pos);
            if (!e.esborrat && e.clau.equals(key)) {
                return e.valor;
            }
            pos = (pos + 1) % midaTaula;
            elementsExplorats++;
        }
        throw new ElementNoTrobat();
    }

    @Override
    public void esborrar(K key) throws ElementNoTrobat {
        int pos = funcioHash(key);
        int elementsExplorats = 0;

        while (taula.get(pos) != null && elementsExplorats < midaTaula) {
            Entrada e = taula.get(pos);
            if (!e.esborrat && e.clau.equals(key)) {
                // PUNT D'ESTUDI: Esborrat Lògic.
                // No posem la posició a 'null', només marquem el flag.
                e.esborrat = true;
                numElements--;
                numEsborrats++;
                return;
            }
            pos = (pos + 1) % midaTaula;
            elementsExplorats++;
        }
        throw new ElementNoTrobat();
    }

    @Override
    public boolean buscar(K key) {
        // La lògica de buscar és idèntica a consultar, però retorna boolean
        try {
            consultar(key);
            return true;
        } catch (ElementNoTrobat e) {
            return false;
        }
    }

    @Override
    public boolean esBuida() {
        return numElements == 0;
    }

    @Override
    public int numElements() {
        return numElements;
    }

    @Override
    public K[] obtenirClaus() {
        Object[] claus = new Object[numElements];
        int idx = 0;
        for (Entrada e : taula) {
            // Ignorem els nulls i els marcats com a esborrats
            if (e != null && !e.esborrat) {
                claus[idx++] = e.clau;
            }
        }
        return (K[]) claus;
    }

    @Override
    public float factorCarrega() {
        return (float) this.numElements / (float) this.midaTaula;
    }

    @Override
    public int midaTaula() {
        return midaTaula;
    }

    @Override
    public Iterator<V> iterator() {
        List<V> elements = new ArrayList<>();
        for (Entrada e : taula) {
            if (e != null && !e.esborrat) {
                elements.add(e.valor);
            }
        }
        // Ordenem abans de retornar l'iterador per complir el requisit
        try {
            elements.sort((v1, v2) -> ((Comparable<V>) v1).compareTo(v2));
        } catch (ClassCastException e) {
            System.err.println("Error d'ordenació.");
        }
        return elements.iterator();
    }

    /*

    public int eliminarCanconsArtista(String nomArtista) {
    int canconsEsborrades = 0;

    // A diferència del buscar() o inserir(), aquí l'enunciat ens obliga
    // a recórrer tota la taula de dalt a baix de forma lineal
    for (Entrada e : taula) {

        // CONDICIÓ CRÍTICA: Ignorem forats buits (null) i cançons ja esborrades abans
        if (e != null && !e.esborrat) {

            // Si és l'artista que busquem...
            if (e.valor.getArtista().equalsIgnoreCase(nomArtista)) {

                // ESBORRAT LÒGIC (El Tombstone)
                e.esborrat = true;

                // Actualització obligatòria de la salut de la taula
                numElements--;
                numEsborrats++;
                canconsEsborrades++;
            }
        }
    }

    return canconsEsborrades;
    }
    */

    /*
    public int comptarElementsDesplacats() {
    int desplacats = 0;

    // Utilitzem un for normal perquè ens interessa saber la 'i' (el calaix físic)
    for (int i = 0; i < midaTaula; i++) {
        Entrada e = taula.get(i);

        // Condició d'or: només mirem nodes vius
        if (e != null && !e.esborrat) {

            // On hauria d'anar teòricament aquesta cançó segons la seva clau?
            int calaixTeoric = funcioHash(e.clau);

            // Si el calaix físic ('i') no quadra amb el teòric, és que va col·lisionar
            if (i != calaixTeoric) {
                desplacats++;
            }
        }
    }

    return desplacats;
}
    */
}
