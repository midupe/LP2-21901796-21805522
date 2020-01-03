package pt.ulusofona.lp2.fandeisiaGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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
            tabuleiro[y][x] = id;
        }
        if (moedasRESISTENCIA < 0 && moedasLDR < 0) {
            return 1;
        }
        if (moedasRESISTENCIA < 0) {
            return 3;
        }
        if (moedasLDR < 0) {
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
        turnos++;
        tesouroApanhadoCurrentTurn = 0;
        for (Creature creature: criaturas) {
            int id = creature.getId();
            String orientacao = creature.getOrientacao();
            Boolean moveDiagonal = creature.getMoverDiagonal();
            if (!validarEMoverCriatura(id)){
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
                creature.getImagePNG();
            }
        }
        if (currentTeam == 10){
            if (tesouroApanhadoCurrentTurn > 0) {
                moedasLDR += 2;
            } else {
                moedasLDR++;
            }
            currentTeam = 20;
        }
        if (currentTeam == 20){
            if (tesouroApanhadoCurrentTurn > 0) {
                moedasRESISTENCIA += 2;
            } else {
                moedasRESISTENCIA++;
            }
            currentTeam = 10;
        }
    }

    public List<Creature> getCreatures() {
        return criaturas;
    }

    public boolean gameIsOver() {
        if (tesouros.isEmpty()) {
            return true;
        }
        if (turnos == 15 && getCurrentScore(20) == 0 && getCurrentScore(10) == 0) {
            return true;
        }
        int pontosEmJogo = 0;
        for (Tesouro tesouro: tesouros){
            pontosEmJogo += tesouro.getPontos();
        }
        if (pontosEmJogo/2 < getCurrentScore(20) || pontosEmJogo/2 < getCurrentScore(10)) {
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
            resultados.add(creature.getId() + " : " + creature.getTipo() + " : " + creature.getOuros()+ " : " +
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

    public boolean enchant(int x, int y, String spellName){
        if (spellName == null) {
            return false;
        }
        for (Creature creature: criaturas){
            if (creature.getX() == x && y == creature.getY()) {
                int id = creature.getId();
                if (spellName.equals("EmpurraParaNorte") && y != 0 && !temBuraco(x,y-1) && !temCriatura(x, y-1)){
                    if (gastarMoedas(1)) {
                        moverCriatura(id, x, y - 1);
                        tabuleiro[y][x] = 0;
                        return true;
                    }
                }
                if (spellName.equals("EmpurraParaEste") && x != widthX && !temBuraco(x+1,y) && !temCriatura(x+1, y)){
                    if (gastarMoedas(1)) {
                        moverCriatura(id, x + 1, y);
                        tabuleiro[y][x] = 0;
                        return true;
                    }
                }
                if (spellName.equals("EmpurraParaSul") && y != heightY && !temBuraco(x, y+1) && !temCriatura(x, y+1)){
                    if (gastarMoedas(1)) {
                        moverCriatura(id, x, y + 1);
                        tabuleiro[y][x] = 0;
                        return true;
                    }
                }

                if (spellName.equals("EmpurraParaOeste") && x != 0 && !temBuraco(x-1, y) && !temCriatura(x-1, y)){
                    if (gastarMoedas(1)) {
                        moverCriatura(id, x - 1, y);
                        tabuleiro[y][x] = 0;
                        return true;
                    }
                }
                if (spellName.equals("ReduzAlcance")){
                    if (gastarMoedas(2)) {
                        //Implementar
                        return true;
                    }
                }
                if (spellName.equals("DuplicaAlcance")){
                    if (gastarMoedas(3)) {
                        //Implementar
                        return true;
                    }
                }
                if (spellName.equals("Congela")){
                    if (gastarMoedas(3)) {
                        //Implementar
                        return true;
                    }
                }
                if (spellName.equals("Congela4Ever")){
                    if (gastarMoedas(10)) {
                        //Implementar
                        return true;
                    }
                }
                if (spellName.equals("Descongela")){
                    if (gastarMoedas(8)) {
                        //Implementar
                        return true;
                    }
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

    public boolean temCriatura(int x, int y){
        for (Creature creature: criaturas){
            if (creature.getX() == x && creature.getY() == y){
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

    public boolean gastarMoedas (int quantidade) {
        if (currentTeam == 10 && moedasLDR >= quantidade) {
            moedasLDR -= quantidade;
            return true;
        }
        if (currentTeam == 20 && moedasRESISTENCIA >= quantidade) {
            moedasRESISTENCIA -= quantidade;
        }
        return false;
    }

    public boolean validarEMoverCriatura(int id){
        for (Creature creature: criaturas) {
            if (creature.getId() == id) {
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
                    irY = y-alcance;
                }
                if (orientacao.equals("Sul")) {
                    irX = x;
                    irY = y+alcance;
                }
                if (orientacao.equals("Este")) {
                    irX = x+alcance;
                    irY = y;
                }
                if (orientacao.equals("Oeste")) {
                    irX = x-alcance;
                    irY = y;
                }
                if (orientacao.equals("Nordeste")) {
                    irX = x+alcance;
                    irY = y-alcance;
                }
                if (orientacao.equals("Noroeste")) {
                    irX = x-alcance;
                    irY = y-alcance;
                }
                if (orientacao.equals("Sudeste")) {
                    irX = x-alcance;
                    irY = y+alcance;
                }
                if (orientacao.equals("Sudoeste")) {
                    irX = x-alcance;
                    irY = y+alcance;
                }
                if (tipo.equals("Elfo") && temPersonagemMovElfo(x, y, irX, irY, orientacao)){
                    return false;
                }
                if (tipo.equals("Gigante") && temGigante(x , y, irX, irY)) {
                    return false;
                }
                if (irX >= 0 && irX <= widthX && irY >= 0 && irY <= heightY) {
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

                    if (tabuleiro[irY][irX] == 0 || tesouroApanhado) {
                        creature.setX(irX);
                        creature.setY(irY);
                        tabuleiro[y][x] = 0;
                        tabuleiro[irY][irX] = id;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean temGigante(int x , int y, int irX, int irY) {
        int i = 0;
        if (x < irX) {
            for (i = x + 1; i < irX; i++){
                for (Creature creature: criaturas) {
                    if (creature.getTipo().equals("Gigante") && creature.getX() == i && creature.getY() == y) {
                        return true;
                    }
                }
            }
        }
        if (irX < x) {
            for (i = irX + 1; i < x; i++){
                for (Creature creature: criaturas) {
                    if (creature.getTipo().equals("Gigante") && creature.getX() == i && creature.getY() == y) {
                        return true;
                    }
                }
            }
        }
        if (y < irY) {
            for (i = y + 1; i < irY; i++){
                for (Creature creature: criaturas) {
                    if (creature.getTipo().equals("Gigante") && creature.getX() == x && creature.getY() == i) {
                        return true;
                    }
                }
            }
        }
        if (irY < y) {
            for (i = irY + 1; i < y; i++){
                for (Creature creature: criaturas) {
                    if (creature.getTipo().equals("Gigante") && creature.getX() == x && creature.getY() == i) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean temPersonagemMovElfo(int x, int y, int irX, int irY, String orientacao) {
        int i = 0;
        if (orientacao.equals("Norte") || orientacao.equals("Este") || orientacao.equals("Sul") || orientacao.equals("Oeste")) {
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
        }
        if (orientacao.equals("Nordeste")) {
            for (Creature creature : criaturas) {
                if (creature.getX() == x+1 && creature.getY() == y-1) {
                    return true;
                }
            }
        }
        if (orientacao.equals("Sudeste")) {
            for (Creature creature : criaturas) {
                if (creature.getX() == x+1 && creature.getY() == y+1) {
                    return true;
                }
            }
        }
        if (orientacao.equals("Sudoeste")) {
            for (Creature creature : criaturas) {
                if (creature.getX() == x-1 && creature.getY() == y+1) {
                    return true;
                }
            }
        }
        if (orientacao.equals("Noroeste")) {
            for (Creature creature : criaturas) {
                if (creature.getX() == x-1 && creature.getY() == y-1) {
                    return true;
                }
            }
        }
        return false;
    }
}
