package pt.ulusofona.lp2.fandeisiaGame;

public class Creature {
    int id;
    String tipo;
    int x;
    int y;
    int equipa;
    String orientacao;
    String imagem;
    int pontos = 0;
    boolean moverDiagonal = false;
    int custo;
    int ouros = 0;
    int pratas = 0;
    int bronzes = 0;
    String tipof;
    int numTesourosEncontrados = 0;

    public boolean getMoverDiagonal() {
        return moverDiagonal;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    int alcance;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


    public int getEquipa() {
        return equipa;
    }

    public int getPontos() {
        return pontos;
    }

    public String getTipo() {
        return tipo;
    }

    public String getOrientacao() {
        return orientacao;
    }

    public int getAlcance() {
        return alcance;
    }

    public void setOrientacao(String orientacao) {
        this.orientacao = orientacao;
    }

    public Creature(int id, String tipo, int equipa, int x, int y, String orientacao) {
        this.id = id;
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.equipa = equipa;
        this.orientacao = orientacao;
        this.tipof = tipo;
        if (tipo.equals("Anão")){
            tipof = "Anao";
            alcance = 1;
            custo = 1;
        }
        if (tipo.equals("Dragão")){
            tipof = "Dragao";
            alcance = 3;
            moverDiagonal = true;
            custo = 9;
            //tem particularidades no movimento
        }
        if (tipo.equals("Elfo")){
            alcance = 2;
            moverDiagonal = true;
            custo = 5;
            //tem particularidades no movimento
        }
        if (tipo.equals("Gigante")){
            alcance = 3;
            custo = 5;
            //tem particularidades no movimento
        }
        if (tipo.equals("Humano")){
            alcance = 2;
            custo = 3;
        }
        this.imagem = (tipof + "_" + orientacao + ".png");
        this.getImagePNG();
    }

    public int getCusto() {
        return custo;
    }

    public String getImagePNG() {
        //return imagem; //ESTA COM BUG
        return null;
    }

    public int getId() {
        return id;
    }

    public int getOuros() {
        return ouros;
    }

    public int getPratas() {
        return pratas;
    }

    public int getBronzes() {
        return bronzes;
    }

    public String toString() {
        return id + " | " + tipo + " | " + equipa + " | " + numTesourosEncontrados + " @ (" + x + ", " + y + ") " + orientacao;
    }

    public void adicionarPonto(int pontosAdd) {
        numTesourosEncontrados++;
        if (pontosAdd == 3) {
            ouros++;
        }
        if (pontosAdd == 2) {
            pratas++;
        }
        if (pontosAdd == 1) {
            bronzes++;
        }
        pontos += pontosAdd;
    }
}
