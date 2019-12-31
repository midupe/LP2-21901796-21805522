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
    int alcance;
    boolean moverDiagonal = false;
    int custo;
    String tipof;

    public int getEquipa() {
        return equipa;
    }

    public int getPontos() {
        return pontos;
    }

    public String getTipo() {
        return tipo;
    }

    public Creature(int id, String tipo, int x, int y, int equipa, String orientacao) {
        this.id = id;
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.equipa = equipa;
        this.orientacao = orientacao;
        this.imagem = getImagePNG();
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
    }

    public int getCusto() {
        return custo;
    }

    public String getImagePNG() {
        //imagem = tipof + "_" + orientacao;
        return null;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return id + " | " + tipo + " | " + equipa + " | " + pontos + " @ (" + x + ", " + y + ") " + orientacao;
    }
}
