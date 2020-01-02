package pt.ulusofona.lp2.fandeisiaGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class FandeisiaGameManager {

    static ArrayList<Creature> criaturas = new ArrayList<>();
    static ArrayList<Tesouro> tesouros = new ArrayList<>();
    static ArrayList<Buraco> buracos = new ArrayList<>();
    int widthX;
    int heightY;

    int[][] tabuleiro;
    int moedasLDR = 50;
    int moedasRESISTENCIA = 50;
    int turnos;
    int currentTeam;

    public FandeisiaGameManager() {}

    public String[][] getCreatureTypes() {
        String[][] creaturesType = new String[5][4];
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
        computerArmy.put("Anão", r.nextInt(3));
        computerArmy.put("Dragão", r.nextInt(3));
        computerArmy.put("Elfo", r.nextInt(3));
        computerArmy.put("Gigante", r.nextInt(3));
        computerArmy.put("Humano", r.nextInt(3));
        return computerArmy;
    }

    public int startGame(String[] content, int rows, int columns) {
        this.widthX = columns - 1;
        this.heightY = rows - 1;
        tabuleiro = new int[columns][rows];
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
            } else if (type.equals("hole")){
                x = Integer.parseInt(data[2].replace(" x: ", ""));
                y = Integer.parseInt(data[3].replace(" y: ", ""));
                Buraco hole = new Buraco(id, x,y);
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
            tabuleiro[x][y] = id;
        }
        if (moedasRESISTENCIA < 0 && moedasLDR < 0) {
            criaturas.removeAll(criaturas);
            tesouros.removeAll(tesouros);
            buracos.removeAll(buracos);
            moedasLDR = 50;
            moedasRESISTENCIA = 50;
            return 1;
        }
        if (moedasRESISTENCIA < 0) {
            criaturas.removeAll(criaturas);
            tesouros.removeAll(tesouros);
            buracos.removeAll(buracos);
            moedasLDR = 50;
            moedasRESISTENCIA = 50;
            return 3;
        }
        if (moedasLDR < 0) {
            criaturas.removeAll(criaturas);
            tesouros.removeAll(tesouros);
            buracos.removeAll(buracos);
            moedasLDR = 50;
            moedasRESISTENCIA = 50;
            return 2;
        }
        turnos = 0;
        criaturas.sort(Comparator.comparing(Creature::getId));
        return 0;
    }

    public void setInitialTeam(int teamId) {
        currentTeam = teamId;
    }

    public void processTurn(){
        /*
        ------------ ACAO ------------

        1. Lançar feitiços: 0 ou +
        2. Calcular e aplicar os efeitos dos feitiços aplicados pelo utilizador

        ------------ MOVER ------------

        1. Mover ou tentar: por ordem do id
            a. vericar se movimento valido
            b. se valido mover, se nao, virar

        ------------ MOEDAS E TESOURO ------------

        1. 1MF,  se nenhuma criatura do jogador tiver encontrado um Tesouro no turno em questão
        2. 2MF, se pelo menos uma criatura do jogador tiver encontrado um Tesouro no turno em questão

        */







        if (currentTeam == 10){
            currentTeam = 20;
        } else {
            currentTeam = 10;
        }
    }

    public List<Creature> getCreatures() {
        return criaturas;
    }

    public boolean gameIsOver() {
        if (turnos > 14 && getCurrentScore(1) == 0 && getCurrentScore(0) == 0) {
            return true;
        }
        if (tesouros.size() == getCurrentScore(0) + getCurrentScore(1)) {
            return true;
        }
        if (tesouros.size() / 2 < getCurrentScore(0) || tesouros.size() / 2 < getCurrentScore(1)) {
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

        }
        if (getCurrentScore(10) < getCurrentScore(20)) {
            resultados.add("Resultado: Vitória da equipa RESISTENCIA");
        }
        resultados.add("Nr. de Turnos jogados: " + turnos);
        resultados.add("-----");
        for (Creature creature : criaturas) {
            resultados.add(creature.getId() + " : " + creature.getTipo() + " : " + creature.getPontos());
        }
        return resultados;
    }

    public int getElementId(int x, int y) {
        return tabuleiro[x][y];
    }

    public int getCurrentTeamId() {
        return currentTeam;
    }

    public int getCurrentScore(int teamID) {
        int pontosLDR = 0;
        int pontosRESISTENCIA = 0;
        for (Creature creature : criaturas) {
            if (creature.getEquipa() == 0) {
                pontosLDR += creature.getPontos();
            }
            if (creature.getEquipa() == 1) {
                pontosRESISTENCIA += creature.getPontos();
            }
        }
        if (teamID == 10) {
            return pontosLDR;
        } else {
            return pontosRESISTENCIA;
        }
    }

    public boolean enchant(int x, int y, String spellName){
        if (spellName == null) {
            return false;
        }
        for (Creature creature: criaturas){
            if (creature.getX() == x && y == creature.getY()) {
                int id = creature.getId();
                if (spellName.equals("EmpurraParaNorte") && y != 0 && !temBuraco(x,y-1)){
                    moverCriatura(id, x, y-1);
                    tabuleiro[x][y] = 0;
                    return true;
                }
                if (spellName.equals("EmpurraParaEste") && x != widthX && !temBuraco(x+1,y)){
                    moverCriatura(id, x+1, y);
                    tabuleiro[x][y] = 0;
                    return true;
                }
                if (spellName.equals("EmpurraParaSul") && y != heightY && !temBuraco(x, y+1)){
                    moverCriatura(id, x, y+1);
                    tabuleiro[x][y] = 0;
                    return true;
                }

                if (spellName.equals("EmpurraParaOeste") && x != 0 && !temBuraco(x-1, y)){
                    moverCriatura(id, x-1, y);
                    tabuleiro[x][y] = 0;
                    return true;
                }
                if (spellName.equals("ReduzAlcance")){
                    return true;
                }
                if (spellName.equals("DuplicaAlcance")){
                    return true;
                }
                if (spellName.equals("Congela")){
                    return true;
                }
                if (spellName.equals("Congela4Ever")){
                    return true;
                }
                if (spellName.equals("Descongela")){
                    return true;
                }
            }
        }
        return false;
    }

    public String getSpell(int x, int y) {
        //Implementar
        return null;
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

    //------------- FUNCOES EXTRA -------------\\

    public boolean temBuraco(int x, int y){
        for (Buraco buraco: buracos){
            if (buraco.getX() == x && buraco.getY() == y){
                return true;
            }
        }
        return false;
    }

    public void moverCriatura (int id, int x, int y){
        for (Creature creature: criaturas) {
            if (creature.getId() == id) {
                creature.setX(x);
                creature.setY(y);
                tabuleiro[x][y] = id;
            }
        }
    }
}
