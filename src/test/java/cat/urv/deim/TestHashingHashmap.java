package cat.urv.deim;

import org.junit.jupiter.api.Test;

public class TestHashingHashmap extends TestHashingBase {

    @Override
    protected int tipus() {
        return 3;
    }

    @Test
    public void testHashNegativeSize() {
        testHashNegativeSizeBase();
    }

    @Test
    public void testHashTipusInvalid() {
        testHashTipusInvalidBase();
    }

    @Test
    public void testHashInserir1() {
        testHashInserir1Base();
    }

    @Test
    public void testHashInserir2() {
        testHashInserir2Base();
    }

    @Test
    public void testHashInserir3() {
        testHashInserir3Base();
    }

    @Test
    public void testHashInserir4() {
        testHashInserir4Base();
    }

    @Test
    public void testHashEsborrar1() {
        testHashEsborrar1Base();
    }

    @Test
    public void testHashEsborrar2() {
        testHashEsborrar2Base();
    }

    @Test
    public void testHashEsborrar3() {
        testHashEsborrar3Base();
    }

    @Test
    public void testHashEsborrar4() {
        testHashEsborrar4Base();
    }

    @Test
    public void testHashConsultar1() {
        testHashConsultar1Base();
    }

    @Test
    public void testHashConsultar2() {
        testHashConsultar2Base();
    }

    @Test
    public void testHashConsultar3() {
        testHashConsultar3Base();
    }

    @Test
    public void testHashConsultar4() {
        testHashConsultar4Base();
    }

    @Test
    public void testHashBuscar1() {
        testHashBuscar1Base();
    }

    @Test
    public void testHashBuscar2() {
        testHashBuscar2Base();
    }

    @Test
    public void testHashBuscar3() {
        testHashBuscar3Base();
    }

    @Test
    public void testHashBuscar4() {
        testHashBuscar4Base();
    }

    @Test
    public void testHashEsBuida1() {
        testHashEsBuida1Base();
    }

    @Test
    public void testHashEsBuida2() {
        testHashEsBuida2Base();
    }

    @Test
    public void testHashEsBuida3() {
        testHashEsBuida3Base();
    }

    @Test
    public void testHashMida1() {
        testHashMida1Base();
    }

    @Test
    public void testHashMida2() {
        testHashMida2Base();
    }

    @Test
    public void testHashFactorCarrega1() {
        testHashFactorCarrega1Base();
    }

    @Test
    public void testHashFactorCarrega2() {
        testHashFactorCarrega2Base();
    }

    @Test
    public void testHashFactorCarrega3() {
        testHashFactorCarrega3Base();
    }

    @Test
    public void testHashElements1() {
        testHashElements1Base();
    }

    @Test
    public void testHashElements2() {
        testHashElements2Base();
    }

    @Test
    public void testHashElements3() {
        testHashElements3Base();
    }

    @Test
    public void testHashElements4() {
        testHashElements4Base();
    }

    @Test
    public void testHashPopularitat1() {
        testHashPopularitat1Base();
    }

    @Test
    public void testHashPopularitat2() {
        testHashPopularitat2Base();
    }

    @Test
    public void testHashPopularitat3() {
        testHashPopularitat3Base();
    }

    @Test
    public void testHashPopularitat4() {
        testHashPopularitat4Base();
    }

    @Test
    public void testHashClaus1() {
        testHashClaus1Base();
    }

    @Test
    public void testHashClaus2() {
        testHashClaus2Base();
    }

    @Test
    public void testHashClaus3() {
        testHashClaus3Base();
    }

    @Test
    public void testHashOrdreCanco1() {
        testHashOrdreCanco1Base();
    }

    @Test
    public void testHashOrdreCanco2() {
        testHashOrdreCanco2Base();
    }

    @Test
    public void testHashFitxerInexistent() {
        testHashFitxerInexistentBase();
    }

    @Test
    public void testHashFitxerMalformat() {
        testHashFitxerMalformatBase();
    }

    @Test
    public void testMapaActualitzaValor() throws cat.urv.deim.exceptions.ElementNoTrobat {
        testMapaActualitzaValorBase();
    }

    @Test
    public void testMapaObtenirClausOrdenades() {
        testMapaObtenirClausOrdenadesBase();
    }

    @Test
    public void testMapaObtenirClausDespresEsborrar() throws cat.urv.deim.exceptions.ElementNoTrobat {
        testMapaObtenirClausDespresEsborrarBase();
    }

    @Test
    public void testMapaIteratorOrdenat() {
        testMapaIteratorOrdenatBase();
    }

    @Test
    public void testMapaFactorCarregaDespresEsborrar() throws cat.urv.deim.exceptions.ElementNoTrobat {
        testMapaFactorCarregaDespresEsborrarBase();
    }

    @Test
    public void testMapaColisionsConsultar() throws cat.urv.deim.exceptions.ElementNoTrobat {
        testMapaColisionsConsultarBase();
    }

    @Test
    public void testMapaEsborrarColisioManteAltres() throws cat.urv.deim.exceptions.ElementNoTrobat {
        testMapaEsborrarColisioManteAltresBase();
    }

    @Test
    public void testMapaReinsercioDespresEsborrarColisio() throws cat.urv.deim.exceptions.ElementNoTrobat {
        testMapaReinsercioDespresEsborrarColisioBase();
    }
}
