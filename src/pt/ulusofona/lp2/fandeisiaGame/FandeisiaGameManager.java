package pt.ulusofona.lp2.fandeisiaGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class FandeisiaGameManager {

    ArrayList<Creature> criaturas;
    ArrayList<Tesouro> tesouros;
    ArrayList<Buraco> buracos;
    int widthX;
    int heightY;
    int[][] tabuleiro;
    int moedasLDR = 50;
    int moedasRESISTENCIA = 50;
    int turnos;
    int currentTeam;
    int tesouroApanhadoCurrentTurn;
    int turnoUltimoTesouroApanhado;

    public FandeisiaGameManager() {
    }

    public String[][] getCreatureTypes() {
        String[][] creaturesType = new String[6][4];
        creaturesType[0][0] = "Anão";
        creaturesType[0][1] = "Anao.png";
        creaturesType[0][2] = "Sou pequeno";
        creaturesType[0][3] = "1";
        creaturesType[1][0] = "Dragão";
        creaturesType[1][1] = "Dragao.png";
        creaturesType[1][2] = "Consegue fazer cenas";
        creaturesType[1][3] = "9";
        creaturesType[2][0] = "Elfo";
        creaturesType[2][1] = "Elfo.png";
        creaturesType[2][2] = "Pode voar por cima das outras criaturas";
        creaturesType[2][3] = "5";
        creaturesType[3][0] = "Gigante";
        creaturesType[3][1] = "Gigante.png";
        creaturesType[3][2] = "Sou enorme";
        creaturesType[3][3] = "5";
        creaturesType[4][0] = "Humano";
        creaturesType[4][1] = "Humano.png";
        creaturesType[4][2] = "Tem vida";
        creaturesType[4][3] = "3";
        creaturesType[5][0] = "Druída";
        creaturesType[5][1] = "Druida.png";
        creaturesType[5][2] = "Largo tesouros";
        creaturesType[5][3] = "4";
        return creaturesType;
    }

    public String[][] getSpellTypes() {
        String[][] spellTypes = new String[9][3];
        spellTypes[0][0] = "EmpurraParaNorte";
        spellTypes[0][1] = "Move a criatura 1 unidade para Norte.";
        spellTypes[0][2] = "1";
        spellTypes[1][0] = "EmpurraParaEste";
        spellTypes[1][1] = "Move a criatura 1 unidade para Este.";
        spellTypes[1][2] = "1";
        spellTypes[2][0] = "EmpurraParaSul";
        spellTypes[2][1] = "Move a criatura 1 unidade para Sul.";
        spellTypes[2][2] = "1";
        spellTypes[3][0] = "EmpurraParaOeste";
        spellTypes[3][1] = "Move a criatura 1 unidade para Oeste.";
        spellTypes[3][2] = "1";
        spellTypes[4][0] = "ReduzAlcance";
        spellTypes[4][1] = "Reduz o alcance da criatura para:MIN (alcance original, 1)";
        spellTypes[4][2] = "2";
        spellTypes[5][0] = "DuplicaAlcance";
        spellTypes[5][1] = "Aumenta o alcance da criatura para o dobro.";
        spellTypes[5][2] = "3";
        spellTypes[6][0] = "Congela";
        spellTypes[6][1] = "A criatura alvo não se move neste turno.";
        spellTypes[6][2] = "3";
        spellTypes[7][0] = "Congela4Ever";
        spellTypes[7][1] = "A criatura alvo não se move mais até ao final do jogo.";
        spellTypes[7][2] = "10";
        spellTypes[8][0] = "Descongela";
        spellTypes[8][1] = "Inverte a aplicação de um Feitiço Congela4Ever";
        spellTypes[8][2] = "8";
        return spellTypes;
    }

    public Map<String, Integer> createComputerArmy() {
        Random r = new Random();
        Map<String, Integer> computerArmy = new HashMap<>();
        computerArmy.put("Anão", 1);
        /*computerArmy.put("Anão", r.nextInt(3));
        computerArmy.put("Dragão", r.nextInt(3));
        computerArmy.put("Elfo", r.nextInt(3));
        computerArmy.put("Gigante", r.nextInt(3));
        computerArmy.put("Humano", r.nextInt(3));*/
        return computerArmy;
    }

    public void startGame(String[] content, int rows, int columns) throws InsufficientCoinsException {
        this.widthX = columns - 1;
        this.heightY = rows - 1;
        moedasLDR = 50;
        moedasRESISTENCIA = 50;
        criaturas = new ArrayList<>();
        tesouros = new ArrayList<>();
        buracos = new ArrayList<>();
        tabuleiro = new int[rows][columns];
        for (String object : content) {
            String[] data = object.split(",");
            int id = Integer.parseInt(data[0].replace("id: ", ""));
            String type = data[1].replace(" type: ", "");
            int x;
            int y;
            if (type.equals("gold") || type.equals("silver") || type.equals("bronze")) {
                x = Integer.parseInt(data[2].replace(" x: ", ""));
                y = Integer.parseInt(data[3].replace(" y: ", ""));
                Tesouro tesouro = new Tesouro(id, x, y, type);
                tesouros.add(tesouro);
            } else if (type.equals("hole")) {
                x = Integer.parseInt(data[2].replace(" x: ", ""));
                y = Integer.parseInt(data[3].replace(" y: ", ""));
                Buraco hole = new Buraco(id, x, y);
                buracos.add(hole);
            } else {
                int teamId = Integer.parseInt(data[2].replace(" teamId: ", ""));
                x = Integer.parseInt(data[3].replace(" x: ", ""));
                y = Integer.parseInt(data[4].replace(" y: ", ""));
                String orientacao = data[5].replace(" orientation: ", "");
                Creature creature = new Creature(id, type, teamId, x, y, orientacao);
                criaturas.add(creature);
                if (teamId == 10) {
                    moedasLDR -= creature.getCusto();
                } else {
                    moedasRESISTENCIA -= creature.getCusto();
                }
            }
            tabuleiro[y][x] = id;
        }
        turnos = 0;
        tesouroApanhadoCurrentTurn = 0;
        criaturas.sort(Comparator.comparing(Creature::getId));
        if (moedasRESISTENCIA < 0 || moedasLDR < 0) {
            throw new InsufficientCoinsException(moedasLDR, moedasRESISTENCIA);
        }
    }



    public void setInitialTeam(int teamId) {
        currentTeam = teamId;
    }

    public void processTurn() {
        if (!gameIsOver()) {
            for (Creature creature : criaturas) {
                int id = creature.getId();
                String orientacao = creature.getOrientacao();
                Boolean moveDiagonal = creature.getMoverDiagonal();
                if (!validarEMoverCriatura(id)) {
                    if (!creature.getFeiticoAplicado().equals("Congela") && !creature.isCongela4ever()) {
                        if (!moveDiagonal) {
                            if (orientacao.equals("Norte")) {
                                creature.setOrientacao("Este");
                            }
                            if (orientacao.equals("Este")) {
                                creature.setOrientacao("Sul");
                            }
                            if (orientacao.equals("Sul")) {
                                creature.setOrientacao("Oeste");
                            }
                            if (orientacao.equals("Oeste")) {
                                creature.setOrientacao("Norte");
                            }
                        } else {
                            if (orientacao.equals("Norte")) {
                                creature.setOrientacao("Nordeste");
                            }
                            if (orientacao.equals("Nordeste")) {
                                creature.setOrientacao("Este");
                            }
                            if (orientacao.equals("Este")) {
                                creature.setOrientacao("Sudeste");
                            }
                            if (orientacao.equals("Sudeste")) {
                                creature.setOrientacao("Sul");
                            }
                            if (orientacao.equals("Sul")) {
                                creature.setOrientacao("Sudoeste");
                            }
                            if (orientacao.equals("Sudoeste")) {
                                creature.setOrientacao("Oeste");
                            }
                            if (orientacao.equals("Oeste")) {
                                creature.setOrientacao("Noroeste");
                            }
                            if (orientacao.equals("Noroeste")) {
                                creature.setOrientacao("Norte");
                            }
                        }
                    }
                    creature.getImagePNG();
                }
                creature.resetAlcance();
                System.out.println(creature);
            }
            if (currentTeam == 10) {
                if (tesouroApanhadoCurrentTurn > 0) {
                    turnoUltimoTesouroApanhado = turnos;
                    moedasLDR += 2;
                } else {
                    moedasLDR++;
                }
                currentTeam = 20;
            } else {
                if (tesouroApanhadoCurrentTurn > 0) {
                    turnoUltimoTesouroApanhado = turnos;
                    moedasRESISTENCIA += 2;
                } else {
                    moedasRESISTENCIA++;
                }
                currentTeam = 10;
            }
            tesouroApanhadoCurrentTurn = 0;
            limparFeiticos();
            turnos++;
        }
    }

    public List<Creature> getCreatures() {
        return criaturas;
    }

    public boolean gameIsOver() {
        if (tesouros.isEmpty()) {
            return true;
        }
        if (turnos - turnoUltimoTesouroApanhado == 16) {
            return true;
        }
        int pontosEmJogo = 0;
        int pontosLDR = getCurrentScore(10);
        int pontosRES = getCurrentScore(20);
        for (Tesouro tesouro : tesouros) {
            pontosEmJogo += tesouro.getPontos();
        }
        if (pontosLDR > pontosRES + pontosEmJogo || pontosRES > pontosLDR + pontosEmJogo) {
            return true;
        }
        return false;
    }

    public List<String> getAuthors() {
        List<String> authors = new ArrayList<>();
        authors.add("Miguel Pereira");
        authors.add("Muhammad Iquibal");
        return authors;
    }

    public List<String> getResults() {
        List<String> resultados = new ArrayList<>();
        resultados.add("Welcome to FANDEISIA");
        if (getCurrentScore(10) == getCurrentScore(20)) {
            resultados.add("Resultado: EMPATE");
            resultados.add("LDR: " + getCurrentScore(10));
            resultados.add("RESISTENCIA: " + getCurrentScore(20));
        }
        if (getCurrentScore(10) > getCurrentScore(20)) {
            resultados.add("Resultado: Vitória da equipa LDR");
            resultados.add("LDR: " + getCurrentScore(10));
            resultados.add("RESISTENCIA: " + getCurrentScore(20));
        }
        if (getCurrentScore(10) < getCurrentScore(20)) {
            resultados.add("Resultado: Vitória da equipa RESISTENCIA");
            resultados.add("RESISTENCIA: " + getCurrentScore(20));
            resultados.add("LDR: " + getCurrentScore(10));
        }
        resultados.add("Nr. de Turnos jogados: " + turnos);
        resultados.add("-----");
        for (Creature creature : criaturas) {
            resultados.add(creature.getId() + " : " + creature.getTipo() + " : " + creature.getOuros() + " : " +
                    creature.getPratas() + " : " + creature.getBronzes() + " : " + creature.getPontos());
        }
        return resultados;
    }

    public int getElementId(int x, int y) {
        return tabuleiro[y][x];
    }

    public int getCurrentTeamId() {
        return currentTeam;
    }

    public int getCurrentScore(int teamID) {
        int pontosLDR = 0;
        int pontosRESISTENCIA = 0;
        for (Creature creature : criaturas) {
            if (creature.getEquipa() == 10) {
                pontosLDR += creature.getPontos();
            }
            if (creature.getEquipa() == 20) {
                pontosRESISTENCIA += creature.getPontos();
            }
        }
        if (teamID == 10) {
            return pontosLDR;
        } else {
            return pontosRESISTENCIA;
        }
    }

    public boolean enchant(int x, int y, String spellName) {
        if (spellName == null) {
            return false;
        }
        for (Creature creature : criaturas) {
            if (creature.getX() == x && y == creature.getY()) {
                int id = creature.getId();
                if (spellName.equals("EmpurraParaNorte") && y != 0 && !temBuraco(x, y - 1) && !temCriatura(x, y - 1)) {
                    if (gastarMoedas(1)) {
                        creature.aplicarEfeito("EmpurraParaNorte");
                        moverCriatura(id, x, y - 1);
                        tabuleiro[y][x] = 0;
                        creature.setNumeroFeiticos();
                        return true;
                    }
                }
                if (spellName.equals("EmpurraParaEste") && x != widthX && !temBuraco(x + 1, y) && !temCriatura(x + 1, y)) {
                    if (gastarMoedas(1)) {
                        creature.aplicarEfeito("EmpurraParaEste");
                        moverCriatura(id, x + 1, y);
                        tabuleiro[y][x] = 0;
                        creature.setNumeroFeiticos();
                        return true;
                    }
                }
                if (spellName.equals("EmpurraParaSul") && y != heightY && !temBuraco(x, y + 1) && !temCriatura(x, y + 1)) {
                    if (gastarMoedas(1)) {
                        creature.aplicarEfeito("EmpurraParaSul");
                        moverCriatura(id, x, y + 1);
                        tabuleiro[y][x] = 0;
                        creature.setNumeroFeiticos();
                        return true;
                    }
                }

                if (spellName.equals("EmpurraParaOeste") && x != 0 && !temBuraco(x - 1, y) && !temCriatura(x - 1, y)) {
                    if (gastarMoedas(1)) {
                        creature.aplicarEfeito("EmpurraParaOeste");
                        moverCriatura(id, x - 1, y);
                        tabuleiro[y][x] = 0;
                        creature.setNumeroFeiticos();
                        return true;
                    }
                }
                if (spellName.equals("ReduzAlcance")) {
                    if (gastarMoedas(2)) {
                        int alcance = creature.getAlcance();
                        if (alcance == creature.getAlcanceOriginal()) {
                            alcance = 1;
                        } else {
                            alcance = creature.getAlcanceOriginal();
                        }
                        int irX = x;
                        int irY = y;
                        String orientacao = creature.getOrientacao();
                        if (orientacao.equals("Norte")) {
                            irX = x;
                            irY = y - alcance;
                        }
                        if (orientacao.equals("Sul")) {
                            irX = x;
                            irY = y + alcance;
                        }
                        if (orientacao.equals("Este")) {
                            irX = x + alcance;
                            irY = y;
                        }
                        if (orientacao.equals("Oeste")) {
                            irX = x - alcance;
                            irY = y;
                        }
                        if (orientacao.equals("Nordeste")) {
                            irX = x + alcance;
                            irY = y - alcance;
                        }
                        if (orientacao.equals("Noroeste")) {
                            irX = x - alcance;
                            irY = y - alcance;
                        }
                        if (orientacao.equals("Sudeste")) {
                            irX = x - alcance;
                            irY = y + alcance;
                        }
                        if (orientacao.equals("Sudoeste")) {
                            irX = x - alcance;
                            irY = y + alcance;
                        }
                        if (temBuraco(irX, irY) || temCriatura(irX, irY)) {
                            gastarMoedas(-2);
                            spellName = null;
                            return false;
                        }
                        if (creature.getTipo().equals("Elfo") && temCriaturasEntre(x, y, irX, irY)) {
                            gastarMoedas(-2);
                            spellName = null;
                            return false;
                        }
                        creature.aplicarEfeito("ReduzAlcance");
                        creature.setNumeroFeiticos();
                        return true;
                    }
                }
                if (spellName.equals("DuplicaAlcance")) {
                    if (gastarMoedas(3)) {
                        int alcance = creature.getAlcanceOriginal() * 2;
                        int irX = x;
                        int irY = y;
                        String orientacao = creature.getOrientacao();
                        if (orientacao.equals("Norte")) {
                            irX = x;
                            irY = y - alcance;
                        }
                        if (orientacao.equals("Sul")) {
                            irX = x;
                            irY = y + alcance;
                        }
                        if (orientacao.equals("Este")) {
                            irX = x + alcance;
                            irY = y;
                        }
                        if (orientacao.equals("Oeste")) {
                            irX = x - alcance;
                            irY = y;
                        }
                        if (orientacao.equals("Nordeste")) {
                            irX = x + alcance;
                            irY = y - alcance;
                        }
                        if (orientacao.equals("Noroeste")) {
                            irX = x - alcance;
                            irY = y - alcance;
                        }
                        if (orientacao.equals("Sudeste")) {
                            irX = x - alcance;
                            irY = y + alcance;
                        }
                        if (orientacao.equals("Sudoeste")) {
                            irX = x - alcance;
                            irY = y + alcance;
                        }
                        if (creature.getFeiticoAplicado().equals("DuplicaAlcance")) {
                            spellName = null;
                            return false;
                        }
                        if (creature.getTipo().equals("Elfo") && temCriaturasEntre(x, y, irX, irY)) {
                            gastarMoedas(-3);
                            spellName = null;
                            return false;
                        }
                        if (temBuraco(irX, irY) || temCriatura(irX, irY)) {
                            gastarMoedas(-3);
                            spellName = null;
                            return false;
                        }
                        creature.setNumeroFeiticos();
                        creature.aplicarEfeito("DuplicaAlcance");
                        return true;
                    }
                }
                if (spellName.equals("Congela")) {
                    if (gastarMoedas(3)) {
                        creature.setNumeroFeiticos();
                        creature.aplicarEfeito("Congela");
                        return true;
                    }
                }
                if (spellName.equals("Congela4Ever")) {
                    if (gastarMoedas(10)) {
                        creature.setNumeroFeiticos();
                        creature.aplicarEfeito("Congela4Ever");
                        return true;
                    }
                }
                if (spellName.equals("Descongela")) {
                    if (gastarMoedas(8)) {
                        creature.setNumeroFeiticos();
                        creature.aplicarEfeito("Descongela");
                        return true;
                    }
                }
            }
        }
        spellName = null;
        return false;
    }

    public String getSpell(int x, int y) {
        String feitico = null;
        for (Creature creature : criaturas) {
            if(creature.getFeiticoAplicado().equals("EmpurraParaNorte")){
                if (tabuleiro[y+1][x] == creature.getId() && !creature.getFeiticoAplicado().equals("")) {
                    feitico = creature.getFeiticoAplicado();
                }
            }
            if(creature.getFeiticoAplicado().equals("EmpurraParaSul")){
                if (tabuleiro[y-1][x] == creature.getId() && !creature.getFeiticoAplicado().equals("")) {
                    feitico = creature.getFeiticoAplicado();
                }
            }
            if(creature.getFeiticoAplicado().equals("EmpurraParaEste")){
                if (tabuleiro[y][x-1] == creature.getId() && !creature.getFeiticoAplicado().equals("")) {
                    feitico = creature.getFeiticoAplicado();
                }
            }
            if(creature.getFeiticoAplicado().equals("EmpurraParaOeste")){
                if (tabuleiro[y][x+1] == creature.getId() && !creature.getFeiticoAplicado().equals("")) {
                    feitico = creature.getFeiticoAplicado();
                }
            }
            if (tabuleiro[y][x] == creature.getId() && !creature.getFeiticoAplicado().equals("")) {
                feitico = creature.getFeiticoAplicado();
            }
        }
        return feitico;
    }

    public int getCoinTotal(int teamID) {
        if (teamID == 10) {
            return moedasLDR;
        } else {
            return moedasRESISTENCIA;
        }

    }

    public boolean saveGame(File fich) {
        //Implementar
        try {
            if (fich.createNewFile()) {
                System.out.println("Ficheiro criado: " + fich.getName());
            } else {
                System.out.println("Ficheiro já existe.");
            }
            return true;
        } catch (IOException e) {
            System.out.println("Ocorreu um erro.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadGame(File fich) {
        //Implementar
        try {
            Scanner myReader = new Scanner(fich);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
            }
            System.out.println("Ficheiro lido sem falhas.");
            myReader.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Ocorreu um erro.");
            e.printStackTrace();
            return false;
        }
    }

    public String whoIsLordEder() {
        return "Éderzito António Macedo Lopes";
    }

    public void toggleAI(boolean active) {
        //Implementar
    }

    public Map<String, List<String>> getStatistics() {
        Map<String, List<String>> statistics = new HashMap<>();

        List<String> maisCarregadas = new ArrayList<>();
        //corrigir - implementar
        criaturas.stream()
                .sorted((c2,c1) -> c1.totalTesouros() - c2.totalTesouros())
                .limit(3)
                .forEach(c->maisCarregadas.add(c.getId()+ ":" + c.totalTesouros()));

        List<String> maisRicas = new ArrayList<>();
        criaturas.stream()
                .sorted((c2,c1) -> c1.getPontos() - c2.getPontos())
                .limit(5)
                .forEach(c->maisRicas.add(c.getId()+ ":"+ c.getPontos()+ ":"+c.getTesouros()));
                Comparator<Creature> pontos = Comparator
                .comparing(Creature::getPontos);
                Comparator<Creature> tesouros = Comparator
                .comparing(Creature::getTesouros);
                Collections.sort(criaturas,tesouros);

        List<String> alvosFavoritos = new ArrayList<>();
        criaturas.stream()
                .sorted((c2,c1) -> c1.getNumeroFeiticos() - c2.getNumeroFeiticos())
                .limit(3)
                .forEach(c->alvosFavoritos.add(c.getId()+ ":"+ c.getEquipa() + ":"+ c.getNumeroFeiticos()));

        List<String> asMaisViajadas = new ArrayList<>();
        //implementar

        List<String> tiposDeCriaturaESeusTesouros = new ArrayList<>();
        //implementar

        List<String> viradosPara = new ArrayList<>();
        long countNorte =
                criaturas.stream()
                        .filter((r)-> r.getOrientacao().equals("Norte"))
                        .count();

        long countSul =
                criaturas.stream()
                        .filter((r)-> r.getOrientacao().equals("Sul"))
                        .count();

        long countNordeste =
                criaturas.stream()
                        .filter((r)-> r.getOrientacao().equals("Nordeste"))
                        .count();

        long countNoroeste =
                criaturas.stream()
                        .filter((r)-> r.getOrientacao().equals("Noroeste"))
                        .count();

        long countSudeste =
                criaturas.stream()
                        .filter((r)-> r.getOrientacao().equals("Sudeste"))
                        .count();

        long countSudoeste =
                criaturas.stream()
                        .filter((r)-> r.getOrientacao().equals("Sudoeste"))
                        .count();

        long countOeste =
                criaturas.stream()
                        .filter((r)-> r.getOrientacao().equals("Oeste"))
                        .count();

        long countEste =
                criaturas.stream()
                        .filter((r) -> r.getOrientacao().equals("Este"))
                        .count();

        Map<String, Long> mapa = new HashMap<>();
        mapa.put("Sul",countSul);
        mapa.put("Norte",countNorte);
        mapa.put("Nordeste",countNordeste);
        mapa.put("Noroeste",countNoroeste);
        mapa.put("Sudeste",countSudeste);
        mapa.put("Sudoeste",countSudoeste);
        mapa.put("Este",countEste);
        mapa.put("Oeste",countOeste);


        List<String> nrVirados = mapa.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Long>comparingByValue().reversed()).thenComparing(Map.Entry.comparingByKey())).
                        map(c1 -> c1.getKey() + ":" + c1.getValue()).collect(Collectors.toList());

        List<String> asMaisEficientes = new ArrayList<>();
        //Implementar dp do movimentar estar feito

        statistics.put("as3MaisCarregadas", maisCarregadas);
        statistics.put("as5MaisRicas", maisRicas);
        statistics.put("osAlvosFavoritos", alvosFavoritos);
        statistics.put("as3MaisViajadas", asMaisViajadas);
        statistics.put("viradosPara", nrVirados);
        statistics.put("asMaisEficientes", asMaisEficientes);
        return statistics;
    }

    //------------- FUNCOES EXTRA -------------\\

    public boolean temBuraco(int x, int y) {
        for (Buraco buraco : buracos) {
            if (buraco.getX() == x && buraco.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public boolean temCriatura(int x, int y) {
        for (Creature creature : criaturas) {
            if (creature.getX() == x && creature.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void moverCriatura(int id, int x, int y) {
        for (Creature creature : criaturas) {
            if (creature.getId() == id) {
                creature.setX(x);
                creature.setY(y);
                List<Tesouro> toRemove = new ArrayList<Tesouro>();
                for (Tesouro tesouro : tesouros) {
                    if (tabuleiro[y][x] == tesouro.getId()) {
                        if (creature.getEquipa() == currentTeam) {
                            tesouroApanhadoCurrentTurn++;
                        }
                        creature.adicionarPonto(tesouro.getPontos());
                        toRemove.add(tesouro);
                    }
                }
                tabuleiro[y][x] = id;
                tesouros.removeAll(toRemove);
            }
        }
    }

    public boolean gastarMoedas(int quantidade) {
        if (currentTeam == 10 && moedasLDR >= quantidade) {
            moedasLDR -= quantidade;
            return true;
        }
        if (currentTeam == 20 && moedasRESISTENCIA >= quantidade) {
            moedasRESISTENCIA -= quantidade;
            return true;
        }
        return false;
    }

    public boolean validarEMoverCriatura(int id) {
        for (Creature creature : criaturas) {
            if (creature.getId() == id && !creature.getFeiticoAplicado().equals("Congela") && !creature.isCongela4ever()) {
                boolean tesouroApanhado = false;
                int alcance = creature.getAlcance();
                int x = creature.getX();
                int y = creature.getY();
                int irX = x;
                int irY = y;
                String tipo = creature.getTipo();
                String orientacao = creature.getOrientacao();
                if (orientacao.equals("Norte")) {
                    irX = x;
                    irY = y - alcance;
                }
                if (orientacao.equals("Sul")) {
                    irX = x;
                    irY = y + alcance;
                }
                if (orientacao.equals("Este")) {
                    irX = x + alcance;
                    irY = y;
                }
                if (orientacao.equals("Oeste")) {
                    irX = x - alcance;
                    irY = y;
                }
                if (orientacao.equals("Nordeste")) {
                    irX = x + alcance;
                    irY = y - alcance;
                }
                if (orientacao.equals("Noroeste")) {
                    irX = x - alcance;
                    irY = y - alcance;
                }
                if (orientacao.equals("Sudeste")) {
                    irX = x - alcance;
                    irY = y + alcance;
                }
                if (orientacao.equals("Sudoeste")) {
                    irX = x - alcance;
                    irY = y + alcance;
                }
                if (tipo.equals("Elfo") && temCriaturasEntre(x, y, irX, irY)) {
                    return false;
                }
                if (tipo.equals("Gigante") && temGigante(x, y, irX, irY)) {
                    return false;
                }
                if (temBuraco(irX, irY)){
                    return false;
                }
                if (irX >= 0 && irX <= widthX && irY >= 0 && irY <= heightY && tabuleiro[irY][irX] <= 0) {
                    if (tabuleiro[irY][irX] != 0) {
                        List<Tesouro> toRemove = new ArrayList<Tesouro>();
                        for (Tesouro tesouro : tesouros) {
                            if (tabuleiro[irY][irX] == tesouro.getId()) {
                                if (creature.getEquipa() == currentTeam) {
                                    tesouroApanhadoCurrentTurn++;
                                }
                                creature.adicionarPonto(tesouro.getPontos());
                                tesouroApanhado = true;
                                toRemove.add(tesouro);
                            }
                        }
                        tesouros.removeAll(toRemove);
                    }
                    creature.setX(irX);
                    creature.setY(irY);
                    tabuleiro[y][x] = 0;
                    tabuleiro[irY][irX] = id;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean temGigante(int x, int y, int irX, int irY) {
        int i = 0;
        if (x < irX) {
            for (i = x + 1; i < irX; i++) {
                for (Creature creature : criaturas) {
                    if (creature.getTipo().equals("Gigante") && creature.getX() == i && creature.getY() == y) {
                        return true;
                    }
                }
            }
        }
        if (irX < x) {
            for (i = irX + 1; i < x; i++) {
                for (Creature creature : criaturas) {
                    if (creature.getTipo().equals("Gigante") && creature.getX() == i && creature.getY() == y) {
                        return true;
                    }
                }
            }
        }
        if (y < irY) {
            for (i = y + 1; i < irY; i++) {
                for (Creature creature : criaturas) {
                    if (creature.getTipo().equals("Gigante") && creature.getX() == x && creature.getY() == i) {
                        return true;
                    }
                }
            }
        }
        if (irY < y) {
            for (i = irY + 1; i < y; i++) {
                for (Creature creature : criaturas) {
                    if (creature.getTipo().equals("Gigante") && creature.getX() == x && creature.getY() == i) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void limparFeiticos() {
        for (Creature creature : criaturas) {
            if (!creature.getFeiticoAplicado().equals("Congela4Ever")) {
                creature.aplicarEfeito("");
            }
        }
    }

    public boolean temCriaturasEntre(int x, int y, int irX, int irY) {
        int i = 0;
        int j = 0;
        if (x < irX && y < irY) {
            for (i = x + 1; i < irX; i++) {
                for (j = y + 1; j < irY; j++) {
                    for (Creature creature : criaturas) {
                        if (creature.getX() == i && creature.getY() == j) {
                            return true;
                        }
                    }
                }
            }
        }
        if (x > irX && y > irY) {
            for (i = irX + 1; i < x; i++) {
                for (j = irY + 1; j < y; j++) {
                    for (Creature creature : criaturas) {
                        if (creature.getX() == i && creature.getY() == j) {
                            return true;
                        }
                    }
                }
            }
        }
        if (x > irX && y < irY) {
            for (i = irX + 1; i < x; i++) {
                for (j = y + 1; j < irY; j++) {
                    for (Creature creature : criaturas) {
                        if (creature.getX() == i && creature.getY() == j) {
                            return true;
                        }
                    }
                }
            }
        }
        if (x < irX && y > irY) {
            for (i = x + 1; i < irX; i++) {
                for (j = irY + 1; j < y; j++) {
                    for (Creature creature : criaturas) {
                        if (creature.getX() == i && creature.getY() == j) {
                            return true;
                        }
                    }
                }
            }
        }
        if (x < irX) {
            for (i = x + 1; i < irX; i++) {
                for (Creature creature : criaturas) {
                    if (creature.getX() == i && creature.getY() == y) {
                        return true;
                    }
                }
            }
        }
        if (irX < x) {
            for (i = irX + 1; i < x; i++) {
                for (Creature creature : criaturas) {
                    if (creature.getX() == i && creature.getY() == y) {
                        return true;
                    }
                }
            }
        }
        if (y < irY) {
            for (i = y + 1; i < irY; i++) {
                for (Creature creature : criaturas) {
                    if (creature.getX() == x && creature.getY() == i) {
                        return true;
                    }
                }
            }
        }
        if (irY < y) {
            for (i = irY + 1; i < y; i++) {
                for (Creature creature : criaturas) {
                    if (creature.getX() == x && creature.getY() == i) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
