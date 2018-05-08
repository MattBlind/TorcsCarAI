package champ2010client.GA;

public class Stats {

    public double[][] statMatrix;
    public int columns;
    public int rows;
    public int failCount;

    public Stats (int rows, int columns){
        this.columns = columns+3;
        this.rows = rows;
        failCount = 0;
        statMatrix = new double[this.rows][this.columns];
    }

    public void addData(int column, int row, double data){
        statMatrix[row][column] = data;
    }

    public void fail(){ failCount++;}

    public void processData(){
        for(int i = 0; i<rows; i++){
            double sum = 0;
            double min = statMatrix[i][0];
            for(int j = 0; j<columns-3; j++){
                sum += statMatrix[i][j];
                if(statMatrix[i][j]<min) min = statMatrix[i][j];
            }
            statMatrix[i][columns-3] = sum / (columns-3);
            statMatrix[i][columns-2] = min;
            statMatrix[i][columns-1] = failCount;
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
