package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementació d'una taula de hash utilitzant ENCADENAMENT INDIRECTE (Separate Chaining).
 * Es fa servir un ArrayList estàtic com a estructura principal, on cada posició
 * actua com a capçalera d'una llista enllaçada programada manualment.
 * @param <K> Tipus genèric per a les claus
 * @param <V> Tipus genèric per als valors emmagatzemats
 */
public class HashingIndirecte<K, V> implements IHashMap<K, V> {

    /**
     * Classe interna privada. Funciona com la "capsa" que guarda les dades de cada element.
     * En ser una llista enllaçada SIMPLE, només necessitem un punter al següent element.
     */
    private class Node {
        K clau;
        V valor;
        Node seguent;   // Punter al següent Node de la mateixa llista (mateix índex de la taula)

        public Node(K clau, V valor) {
            this.clau = clau;
            this.valor = valor;
            this.seguent = null; // Per defecte, quan es crea, no apunta enlloc
        }
    }

    private ArrayList<Node> taula;  // L'estructura principal (els "calaixos")
    private int midaTaula;          // La quantitat de "calaixos" disponibles
    private int numElements;        // Quants objectes (Nodes) hem guardat en total

    /**
     * Constructor
     */
    public HashingIndirecte(int mida) {
        this.midaTaula = mida;
        this.numElements = 0;
        this.taula = new ArrayList<>(mida);

        // PUNT D'ESTUDI: Per què fem això?
        // Un ArrayList a Java neix buit encara que li passis una capacitat inicial.
        // Si no l'omplim de 'nulls', en fer taula.get(3) ens donarà un IndexOutOfBoundsException
        // perquè la posició 3 tècnicament encara no existeix.
        for (int i = 0; i < mida; i++) {
            this.taula.add(null);
        }
    }

    /**
     * PUNT D'ESTUDI: La Funció Hash.
     * Converteix la clau (ex: "Bohemian Rhapsody") en un número d'índex vàlid per a la nostra taula.
     */
    private int funcioHash(K clau) {
        // 1. clau.hashCode() genera un número enter (pot ser negatiu!).
        // 2. Math.abs() el converteix a positiu.
        // 3. % midaTaula (Mòdul) garanteix que el resultat estigui entre 0 i (midaTaula - 1).
        return Math.abs(clau.hashCode() % midaTaula);
    }

    /**
     * Mètode crític: Rehashing. S'executa quan la taula està massa plena (factor de càrrega > 0.75).
     */
    private void redimensionar() {
        int novaMida = midaTaula * 2; // Dupliquem la mida per reduir les col·lisions
        ArrayList<Node> novaTaula = new ArrayList<>(novaMida);

        for (int i = 0; i < novaMida; i++) {
            novaTaula.add(null);
        }

        int midaAntiga = midaTaula;
        // PUNT D'ESTUDI: Actualitzem 'midaTaula' ARA MATEIX perquè funcioHash() fa servir aquesta variable,
        // i necessitem que ens calculi les noves posicions basant-se en la nova capacitat.
        midaTaula = novaMida;

        // Recorrem tota la taula antiga calaix per calaix
        for (int i = 0; i < midaAntiga; i++) {
            Node actual = taula.get(i);

            // Recorrem la llista enllaçada que hi hagi en el calaix actual
            while (actual != null) {
                Node seguent = actual.seguent; // Guardem el pròxim node abans de trencar l'enllaç actual

                // Recalculem on ha d'anar aquest node a la nova taula
                int pos = funcioHash(actual.clau);

                // Inserció al CAP de la nova llista (és més ràpid que buscar el final de la llista)
                actual.seguent = novaTaula.get(pos);
                novaTaula.set(pos, actual);

                actual = seguent; // Avancem en la llista antiga
            }
        }
        taula = novaTaula; // Substituïm la taula vella per la nova
    }

    @Override
    public void inserir(K key, V value) {
        // 1. Control del factor de càrrega
        if (factorCarrega() > 0.75f) {
            redimensionar();
        }

        // 2. Trobem l'índex
        int pos = funcioHash(key);
        Node actual = taula.get(pos);

        // 3. Comprovem si la clau ja existeix (per sobreescriure-la)
        while (actual != null) {
            if (actual.clau.equals(key)) {
                actual.valor = value;
                return; // Si l'hem actualitzat, ja hem acabat
            }
            actual = actual.seguent;
        }

        // 4. Si arribem aquí, la clau és nova.
        // Creem el node i l'inserim al PRINCIPI de la llista d'aquest índex (LIFO).
        Node nouNode = new Node(key, value);
        nouNode.seguent = taula.get(pos);   // El nou node apunta a l'antic "primer" de la llista
        taula.set(pos, nouNode);            // El calaix ara apunta al nostre nou node
        numElements++;
    }

    @Override
    public V consultar(K key) throws ElementNoTrobat {
        int pos = funcioHash(key);
        Node actual = taula.get(pos);

        // Recorrem la llista d'aquesta posició fins a trobar la clau
        while (actual != null) {
            if (actual.clau.equals(key)) {
                return actual.valor;
            }
            actual = actual.seguent;
        }
        throw new ElementNoTrobat(); // Si el bucle acaba i no l'hem trobat, llancem error
    }

    @Override
    public void esborrar(K key) throws ElementNoTrobat {
        int pos = funcioHash(key);
        Node actual = taula.get(pos);
        Node anterior = null; // Necessitem un punter al node anterior per poder "cosir" la llista en esborrar

        while (actual != null) {
            if (actual.clau.equals(key)) {

                // CAS 1: L'element a esborrar és el PRIMER de la llista
                if (anterior == null) {
                    taula.set(pos, actual.seguent); // El calaix passa a apuntar al segon element
                }
                // CAS 2: L'element és al mig o al final
                else {
                    anterior.seguent = actual.seguent; // El node anterior es salta l'actual, desenllaçant-lo.
                }

                numElements--;
                return;
            }
            // Avancem tots dos punters una posició
            anterior = actual;
            actual = actual.seguent;
        }
        throw new ElementNoTrobat();
    }

    @Override
    public boolean buscar(K key) {
        int pos = funcioHash(key);
        Node actual = taula.get(pos);

        while (actual != null) {
            if (actual.clau.equals(key)) {
                return true;
            }
            actual = actual.seguent;
        }
        return false;
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

        // Bucle doble: primer recorrem els calaixos, després la llista de cada calaix
        for (int i = 0; i < midaTaula; i++) {
            Node actual = taula.get(i);
            while (actual != null) {
                claus[idx++] = actual.clau;
                actual = actual.seguent;
            }
        }
        return (K[]) claus;
    }

    @Override
    public float factorCarrega() {
        return (float) numElements / (float) midaTaula;
    }

    @Override
    public int midaTaula() {
        return midaTaula;
    }

    @Override
    public Iterator<V> iterator() {
        List<V> elements = new ArrayList<>();

        for (int i = 0; i < midaTaula; i++) {
            Node actual = taula.get(i);
            while (actual != null) {
                elements.add(actual.valor);
                actual = actual.seguent;
            }
        }

        try {
            elements.sort((v1, v2) -> ((Comparable<V>) v1).compareTo(v2));
        } catch (ClassCastException e) {
            System.err.println("Els elements no es poden ordenar perque no implementen Comparable.");
        }

        return elements.iterator();
    }

    /*

    public int longitudLlistaMaxima() {
    int maxLongitud = 0; // Guardarà el rècord

    // Bucle 1: Recórrer l'armari sencer
    for (int i = 0; i < midaTaula; i++) {
        Node actual = taula.get(i);
        int comptadorCalaix = 0;

        // Bucle 2: Recórrer la llista d'aquest calaix concret
        while (actual != null) {
            comptadorCalaix++;
            actual = actual.seguent; // Avancem per la cadena
        }

        // Si aquest calaix té més cançons que el nostre rècord, l'actualitzem
        if (comptadorCalaix > maxLongitud) {
            maxLongitud = comptadorCalaix;
        }
    }

    return maxLongitud;
    }
     */

    /*
    public int esborrarAntigues(int anyLimit) {
    int esborrades = 0;

    for (int i = 0; i < midaTaula; i++) {
        Node actual = taula.get(i);
        Node anterior = null; // Ens guardarà l'esquena

        while (actual != null) {
            if (actual.valor.getAny() < anyLimit) {
                // EUREKA! L'hem d'esborrar.

                if (anterior == null) {
                    // CAS A: És el primer de la llista (el Cap).
                    // El calaix s'ha de connectar al següent element.
                    taula.set(i, actual.seguent);
                } else {
                    // CAS B: Està al mig o al final.
                    // Saltem el node actual unint l'anterior amb el següent.
                    anterior.seguent = actual.seguent;
                }

                numElements--; // Actualitzem la salut de la taula
                esborrades++;
                actual = actual.seguent; // Avancem sense canviar l'anterior
            } else {
                // No l'esborrem. Avancem normalment.
                anterior = actual;
                actual = actual.seguent;
            }
        }
    }
    return esborrades;
    }
    */
}
