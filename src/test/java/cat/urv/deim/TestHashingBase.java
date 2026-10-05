package cat.urv.deim;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import cat.urv.deim.exceptions.ElementNoTrobat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public abstract class TestHashingBase {

    protected static final String FITXER_CANCONS = "songs1000.csv";

    protected static final class ClauColisio implements Comparable<ClauColisio> {
        private final String valor;
        private final int hashFix;

        private ClauColisio(String valor, int hashFix) {
            this.valor = valor;
            this.hashFix = hashFix;
        }

        @Override
        public int compareTo(ClauColisio altra) {
            return valor.compareTo(altra.valor);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ClauColisio altra)) {
                return false;
            }
            return Objects.equals(valor, altra.valor);
        }

        @Override
        public int hashCode() {
            return hashFix;
        }
    }

    protected abstract int tipus();

    protected HashingCancons crearHash(int mida) {
        return new HashingCancons(mida, tipus());
    }

    protected HashingCancons crearHash(int mida, String fitxer) {
        return new HashingCancons(mida, fitxer, tipus());
    }

    protected Canco crearCancoProva() {
        return new Canco(9999999, "A Test Song", "ArtistaProva", "Rock", 2026, 180, 88);
    }

    protected Canco crearCanco(int id, String titol, String artista, int popularitat) {
        return new Canco(id, titol, artista, "Rock", 2026, 180, popularitat);
    }

    protected String[] extreureTitols(Canco[] cancons) {
        String[] titols = new String[cancons.length];
        for (int i = 0; i < cancons.length; i++) {
            titols[i] = cancons[i].getTitol();
        }
        return titols;
    }

    protected IHashMap<String, Integer> crearMapaString(int mida) {
        return switch (tipus()) {
            case 1 -> new HashingNoEncadenat<>(mida);
            case 2 -> new HashingIndirecte<>(mida);
            case 3 -> new HashingHashmap<>(mida);
            default -> throw new IllegalArgumentException("Tipus de taula de hash incorrecte");
        };
    }

    protected IHashMap<ClauColisio, Integer> crearMapaColisions(int mida) {
        return switch (tipus()) {
            case 1 -> new HashingNoEncadenat<>(mida);
            case 2 -> new HashingIndirecte<>(mida);
            case 3 -> new HashingHashmap<>(mida);
            default -> throw new IllegalArgumentException("Tipus de taula de hash incorrecte");
        };
    }

    protected ClauColisio clau(String valor, int hashFix) {
        return new ClauColisio(valor, hashFix);
    }

    protected Integer[] valorsIterador(IHashMap<?, Integer> mapa) {
        ArrayList<Integer> valors = new ArrayList<>();
        for (Integer valor : mapa) {
            valors.add(valor);
        }
        return valors.toArray(new Integer[0]);
    }

    protected void testHashNegativeSizeBase() {
        assertThrows(IllegalArgumentException.class, () -> new HashingCancons(-200, tipus()));
    }

    protected void testHashTipusInvalidBase() {
        assertThrows(IllegalArgumentException.class, () -> new HashingCancons(200, 0));
        assertThrows(IllegalArgumentException.class, () -> new HashingCancons(200, 4));
    }

    protected void testHashInserir1Base() {
        HashingCancons hash = crearHash(200);
        Canco c = crearCancoProva();
        hash.inserir(c);
        assertEquals(1, hash.numElements());
    }

    protected void testHashInserir2Base() {
        HashingCancons hash = crearHash(2000, FITXER_CANCONS);
        Canco c = crearCancoProva();
        hash.inserir(c);
        assertEquals(1001, hash.numElements());
    }

    protected void testHashInserir3Base() {
        HashingCancons hash = crearHash(2000, FITXER_CANCONS);
        Canco c = crearCancoProva();
        hash.inserir(c);
        Canco c2 = new Canco(9999999, "A Test Song", "ArtistaActualitzat", "Rock", 2026, 181, 90);
        hash.inserir(c2);
        assertEquals(1001, hash.numElements());
    }

    protected void testHashInserir4Base() {
        HashingCancons hash = crearHash(200);
        Canco c1 = crearCanco(1, "Repetida", "Artista1", 20);
        Canco c2 = crearCanco(2, "Repetida", "Artista2", 95);
        hash.inserir(c1);
        hash.inserir(c2);

        try {
            Canco consultada = hash.consultar("Repetida");
            assertEquals(1, hash.numElements());
            assertEquals(2, consultada.getId());
            assertEquals("Artista2", consultada.getArtista());
            assertEquals(95, consultada.getPopularitat());
        } catch (ElementNoTrobat e) {
            fail();
        }
    }

    protected void testHashEsborrar1Base() {
        HashingCancons hash = crearHash(200);
        assertThrows(ElementNoTrobat.class, () -> hash.esborrar("NoExisteix"));
    }

    protected void testHashEsborrar2Base() {
        HashingCancons hash = crearHash(200);
        Canco c = crearCancoProva();
        hash.inserir(c);
        try {
            hash.esborrar(c.getTitol());
        } catch (ElementNoTrobat e) {
            fail();
        }
        assertThrows(ElementNoTrobat.class, () -> hash.esborrar(c.getTitol()));
    }

    protected void testHashEsborrar3Base() {
        HashingCancons hash = crearHash(2000, FITXER_CANCONS);
        try {
            hash.esborrar("Bohemian Rhapsody");
            assertThrows(ElementNoTrobat.class, () -> hash.esborrar("Bohemian Rhapsody"));
        } catch (ElementNoTrobat e) {
            fail();
        }
    }

    protected void testHashEsborrar4Base() {
        HashingCancons hash = crearHash(2000);
        for (int i = 0; i < 1400; i++) {
            Canco c = new Canco(1000 + i, "Song" + i, "Artist" + i, "Pop", 2000 + (i % 20), 180 + (i % 90), 10 + (i % 90));
            hash.inserir(c);
        }
        try {
            hash.esborrar("Song1001");
        } catch (ElementNoTrobat e) {
            fail();
        }
    }

    protected void testHashConsultar1Base() {
        HashingCancons hash = crearHash(200);
        assertThrows(ElementNoTrobat.class, () -> hash.consultar("NoExisteix"));
    }

    protected void testHashConsultar2Base() {
        HashingCancons hash = crearHash(200);
        Canco c = crearCancoProva();
        hash.inserir(c);
        try {
            assertTrue(c.equals(hash.consultar("A Test Song")));
        } catch (ElementNoTrobat e) {
            fail();
        }
    }

    protected void testHashConsultar3Base() {
        HashingCancons hash = crearHash(2000, FITXER_CANCONS);
        try {
            hash.esborrar("Bohemian Rhapsody");
            assertThrows(ElementNoTrobat.class, () -> hash.consultar("Bohemian Rhapsody"));
        } catch (ElementNoTrobat e) {
            fail();
        }
    }

    protected void testHashConsultar4Base() {
        HashingCancons hash = crearHash(200);
        Canco c1 = crearCanco(1, "Mateix", "Inicial", 10);
        Canco c2 = crearCanco(2, "Mateix", "Final", 90);
        hash.inserir(c1);
        hash.inserir(c2);

        try {
            Canco consultada = hash.consultar("Mateix");
            assertEquals("Final", consultada.getArtista());
            assertEquals(90, consultada.getPopularitat());
        } catch (ElementNoTrobat e) {
            fail();
        }
    }

    protected void testHashBuscar1Base() {
        HashingCancons hash = crearHash(200);
        Canco c = crearCancoProva();
        assertFalse(hash.buscar(c));
    }

    protected void testHashBuscar2Base() {
        HashingCancons hash = crearHash(200);
        Canco c = crearCancoProva();
        hash.inserir(c);
        assertTrue(hash.buscar(c));
    }

    protected void testHashBuscar3Base() {
        HashingCancons hash = crearHash(200);
        hash.inserir(crearCanco(1, "Clau", "Original", 50));
        assertTrue(hash.buscar(crearCanco(99, "Clau", "Altre", 80)));
    }

    protected void testHashBuscar4Base() {
        HashingCancons hash = crearHash(200);
        hash.inserir(crearCanco(1, "Titol-1", "Original", 50));
        assertFalse(hash.buscar(crearCanco(1, "Titol-2", "Altre", 80)));
    }

    protected void testHashEsBuida1Base() {
        HashingCancons hash = crearHash(2000, FITXER_CANCONS);
        assertFalse(hash.esBuida());
    }

    protected void testHashEsBuida2Base() {
        HashingCancons hash = crearHash(200);
        assertTrue(hash.esBuida());
    }

    protected void testHashEsBuida3Base() {
        HashingCancons hash = crearHash(200);
        Canco c = crearCancoProva();
        hash.inserir(c);
        try {
            hash.esborrar(c.getTitol());
        } catch (ElementNoTrobat e) {
            fail();
        }
        assertTrue(hash.esBuida());
    }

    protected void testHashMida1Base() {
        HashingCancons hash = crearHash(2000, FITXER_CANCONS);
        Canco c = crearCancoProva();
        hash.inserir(c);
        assertEquals(2000, hash.mida());
    }

    protected void testHashMida2Base() {
        HashingCancons hash = crearHash(200);
        assertEquals(200, hash.mida());
    }

    protected void testHashFactorCarrega1Base() {
        HashingCancons hash = crearHash(4000);
        for (int i = 0; i < 1000; i++) {
            Canco c = new Canco(1000 + i, "Song" + i, "Artist" + i, "Pop", 2000, 200, 50);
            hash.inserir(c);
        }
        assertEquals(0.25, hash.factorCarrega());
    }

    protected void testHashFactorCarrega2Base() {
        HashingCancons hash = crearHash(10);
        hash.inserir(crearCanco(1, "A", "ArtistaA", 40));
        hash.inserir(crearCanco(2, "B", "ArtistaB", 50));
        try {
            hash.esborrar("A");
        } catch (ElementNoTrobat e) {
            fail();
        }
        assertEquals(0.1f, hash.factorCarrega());
    }

    protected void testHashFactorCarrega3Base() {
        HashingCancons hash = crearHash(10);
        hash.inserir(crearCanco(1, "A", "ArtistaA", 40));
        hash.inserir(crearCanco(2, "A", "ArtistaB", 90));
        assertEquals(0.1f, hash.factorCarrega());
    }

    protected void testHashElements1Base() {
        HashingCancons hash = crearHash(2000, FITXER_CANCONS);
        Canco[] c = hash.elements();
        assertEquals(1000, c.length);
    }

    protected void testHashElements2Base() {
        HashingCancons hash = crearHash(400);
        Canco c = crearCancoProva();
        hash.inserir(c);
        Canco[] llc = hash.elements();
        assertTrue(c.equals(llc[0]));
    }

    protected void testHashElements3Base() {
        HashingCancons hash = crearHash(200);
        assertEquals(0, hash.elements().length);
    }

    protected void testHashElements4Base() {
        HashingCancons hash = crearHash(200);
        hash.inserir(crearCanco(1, "Gamma", "ArtistaG", 20));
        hash.inserir(crearCanco(2, "Alpha", "ArtistaA", 30));
        hash.inserir(crearCanco(3, "Beta", "ArtistaB", 40));
        assertArrayEquals(new String[]{"Alpha", "Beta", "Gamma"}, extreureTitols(hash.elements()));
    }

    protected void testHashPopularitat1Base() {
        HashingCancons hash = crearHash(400, FITXER_CANCONS);
        Canco[] p = hash.canconsMesPopulars(80);
        assertEquals(132, p.length);
    }

    protected void testHashPopularitat2Base() {
        HashingCancons hash = crearHash(400, FITXER_CANCONS);
        Canco[] p = hash.canconsMesPopulars(90);
        assertEquals(66, p.length);
    }

    protected void testHashPopularitat3Base() {
        HashingCancons hash = crearHash(200);
        hash.inserir(crearCanco(1, "Inferior", "Artista1", 79));
        hash.inserir(crearCanco(2, "Exacta", "Artista2", 80));
        hash.inserir(crearCanco(3, "Superior", "Artista3", 81));
        Canco[] populars = hash.canconsMesPopulars(80);
        assertArrayEquals(new String[]{"Exacta", "Superior"}, extreureTitols(populars));
    }

    protected void testHashPopularitat4Base() {
        HashingCancons hash = crearHash(200);
        hash.inserir(crearCanco(1, "Una", "Artista1", 10));
        hash.inserir(crearCanco(2, "Dues", "Artista2", 20));
        assertEquals(0, hash.canconsMesPopulars(100).length);
    }

    protected void testHashClaus1Base() {
        HashingCancons hash = crearHash(400, FITXER_CANCONS);
        String[] titols = hash.obtenirTitols();
        assertEquals(1000, titols.length);
    }

    protected void testHashClaus2Base() {
        HashingCancons hash = crearHash(200);
        assertEquals(0, hash.obtenirTitols().length);
    }

    protected void testHashClaus3Base() {
        HashingCancons hash = crearHash(200);
        hash.inserir(crearCanco(1, "Gamma", "ArtistaG", 20));
        hash.inserir(crearCanco(2, "Alpha", "ArtistaA", 30));
        hash.inserir(crearCanco(3, "Beta", "ArtistaB", 40));
        assertArrayEquals(new String[]{"Alpha", "Beta", "Gamma"}, hash.obtenirTitols());
    }

    protected void testHashOrdreCanco1Base() {
        HashingCancons hash = crearHash(400, FITXER_CANCONS);
        Canco[] llc = hash.elements();
        assertEquals("Through the Anthem", llc[878].getTitol());
    }

    protected void testHashOrdreCanco2Base() {
        HashingCancons hash = crearHash(400, FITXER_CANCONS);
        Canco c = crearCancoProva();
        hash.inserir(c);
        Canco[] llc = hash.elements();
        assertEquals("Above the Arrow", llc[1].getTitol());
    }

    protected void testHashFitxerInexistentBase() {
        assertThrows(IllegalArgumentException.class, () -> crearHash(200, "fitxer-que-no-existeix.csv"));
    }

    protected void testHashFitxerMalformatBase() {
        try {
            Path csv = Files.createTempFile("hashing-cancons-malformat", ".csv");
            Files.writeString(csv, "id,titol,artista,genere,any,durada,popularitat\n1,Nomes,Quatre,Camps\n");
            assertThrows(IllegalArgumentException.class, () -> crearHash(200, csv.toString()));
        } catch (IOException e) {
            fail();
        }
    }

    protected void testMapaActualitzaValorBase() throws ElementNoTrobat {
        IHashMap<String, Integer> mapa = crearMapaString(10);
        mapa.inserir("b", 1);
        mapa.inserir("b", 9);
        assertEquals(1, mapa.numElements());
        assertEquals(9, mapa.consultar("b"));
    }

    protected void testMapaObtenirClausOrdenadesBase() {
        IHashMap<String, Integer> mapa = crearMapaString(10);
        mapa.inserir("c", 30);
        mapa.inserir("a", 10);
        mapa.inserir("b", 20);
        assertArrayEquals(new String[]{"a", "b", "c"}, mapa.obtenirClaus());
    }

    protected void testMapaObtenirClausDespresEsborrarBase() throws ElementNoTrobat {
        IHashMap<String, Integer> mapa = crearMapaString(10);
        mapa.inserir("c", 30);
        mapa.inserir("a", 10);
        mapa.inserir("b", 20);
        mapa.esborrar("b");
        assertArrayEquals(new String[]{"a", "c"}, mapa.obtenirClaus());
    }

    protected void testMapaIteratorOrdenatBase() {
        IHashMap<String, Integer> mapa = crearMapaString(10);
        mapa.inserir("c", 30);
        mapa.inserir("a", 10);
        mapa.inserir("b", 20);
        assertArrayEquals(new Integer[]{10, 20, 30}, valorsIterador(mapa));
    }

    protected void testMapaFactorCarregaDespresEsborrarBase() throws ElementNoTrobat {
        IHashMap<String, Integer> mapa = crearMapaString(10);
        mapa.inserir("a", 10);
        mapa.inserir("b", 20);
        mapa.esborrar("a");
        assertEquals(0.1f, mapa.factorCarrega());
    }

    protected void testMapaColisionsConsultarBase() throws ElementNoTrobat {
        IHashMap<ClauColisio, Integer> mapa = crearMapaColisions(5);
        ClauColisio a = clau("a", 1);
        ClauColisio b = clau("b", 1);
        ClauColisio c = clau("c", 1);
        mapa.inserir(a, 10);
        mapa.inserir(b, 20);
        mapa.inserir(c, 30);
        assertEquals(10, mapa.consultar(a));
        assertEquals(20, mapa.consultar(b));
        assertEquals(30, mapa.consultar(c));
    }

    protected void testMapaEsborrarColisioManteAltresBase() throws ElementNoTrobat {
        IHashMap<ClauColisio, Integer> mapa = crearMapaColisions(5);
        ClauColisio a = clau("a", 1);
        ClauColisio b = clau("b", 1);
        ClauColisio c = clau("c", 1);
        mapa.inserir(a, 10);
        mapa.inserir(b, 20);
        mapa.inserir(c, 30);
        mapa.esborrar(b);
        assertThrows(ElementNoTrobat.class, () -> mapa.consultar(b));
        assertEquals(10, mapa.consultar(a));
        assertEquals(30, mapa.consultar(c));
    }

    protected void testMapaReinsercioDespresEsborrarColisioBase() throws ElementNoTrobat {
        IHashMap<ClauColisio, Integer> mapa = crearMapaColisions(5);
        ClauColisio a = clau("a", 1);
        ClauColisio b = clau("b", 1);
        ClauColisio c = clau("c", 1);
        ClauColisio d = clau("d", 1);
        mapa.inserir(a, 10);
        mapa.inserir(b, 20);
        mapa.inserir(c, 30);
        mapa.esborrar(b);
        mapa.inserir(d, 40);
        assertEquals(3, mapa.numElements());
        assertEquals(10, mapa.consultar(a));
        assertEquals(30, mapa.consultar(c));
        assertEquals(40, mapa.consultar(d));
    }

    protected void testMapaRedimensionamentConservaElementsBase() throws ElementNoTrobat {
        IHashMap<ClauColisio, Integer> mapa = crearMapaColisions(4);
        for (int i = 0; i < 10; i++) {
            mapa.inserir(clau("k" + i, i % 2), i);
        }
        assertTrue(mapa.midaTaula() > 4);
        for (int i = 0; i < 10; i++) {
            assertEquals(i, mapa.consultar(clau("k" + i, i % 2)));
        }
    }

    protected void testMapaWrapAroundBase() throws ElementNoTrobat {
        IHashMap<ClauColisio, Integer> mapa = crearMapaColisions(3);
        ClauColisio a = clau("a", 2);
        ClauColisio b = clau("b", 2);
        ClauColisio c = clau("c", 2);
        ClauColisio d = clau("d", 2);
        mapa.inserir(a, 10);
        mapa.inserir(b, 20);
        mapa.inserir(c, 30);
        mapa.esborrar(a);
        mapa.inserir(d, 40);
        assertEquals(20, mapa.consultar(b));
        assertEquals(30, mapa.consultar(c));
        assertEquals(40, mapa.consultar(d));
    }

    protected void testMapaEsborrarCapCadenaBase() throws ElementNoTrobat {
        IHashMap<ClauColisio, Integer> mapa = crearMapaColisions(5);
        ClauColisio a = clau("a", 1);
        ClauColisio b = clau("b", 1);
        ClauColisio c = clau("c", 1);
        mapa.inserir(a, 10);
        mapa.inserir(b, 20);
        mapa.inserir(c, 30);
        mapa.esborrar(c);
        assertThrows(ElementNoTrobat.class, () -> mapa.consultar(c));
        assertEquals(10, mapa.consultar(a));
        assertEquals(20, mapa.consultar(b));
    }

    protected void testMapaEsborrarMigCadenaBase() throws ElementNoTrobat {
        IHashMap<ClauColisio, Integer> mapa = crearMapaColisions(5);
        ClauColisio a = clau("a", 1);
        ClauColisio b = clau("b", 1);
        ClauColisio c = clau("c", 1);
        mapa.inserir(a, 10);
        mapa.inserir(b, 20);
        mapa.inserir(c, 30);
        mapa.esborrar(b);
        assertThrows(ElementNoTrobat.class, () -> mapa.consultar(b));
        assertEquals(10, mapa.consultar(a));
        assertEquals(30, mapa.consultar(c));
    }

    protected void testMapaEsborrarCuaCadenaBase() throws ElementNoTrobat {
        IHashMap<ClauColisio, Integer> mapa = crearMapaColisions(5);
        ClauColisio a = clau("a", 1);
        ClauColisio b = clau("b", 1);
        ClauColisio c = clau("c", 1);
        mapa.inserir(a, 10);
        mapa.inserir(b, 20);
        mapa.inserir(c, 30);
        mapa.esborrar(a);
        assertThrows(ElementNoTrobat.class, () -> mapa.consultar(a));
        assertEquals(20, mapa.consultar(b));
        assertEquals(30, mapa.consultar(c));
    }
}
