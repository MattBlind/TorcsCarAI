package champ2010client.GA;

public class Stats {

    public double[][] statMatrix;
    public int columns;
    public int rows;

    public Stats (int rows, int columns){
        this.columns = columns+2;
        this.rows = rows;
        statMatrix = new double[this.rows][this.columns];
    }

    public void addData(int column, int row, double data){
        statMatrix[row][column] = data;
    }

    public void processData(){
        for(int i = 0; i<rows; i++){
            double sum = 0;
            double min = statMatrix[i][0];
            for(int j = 0; j<columns-2; j++){
                sum += statMatrix[i][j];
                if(statMatrix[i][j]<min) min = statMatrix[i][j];
            }
            statMatrix[i][columns-2] = sum / (columns-2);
            statMatrix[i][columns-1] = min;
        }
    }

    @Override
    public String toString() {
        String retString = "";
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<columns; j++){
                retString = retString+statMatrix[i][j]+" ";
            }
            retString = retString+"\n";
        }
        return retString;
    }
}
