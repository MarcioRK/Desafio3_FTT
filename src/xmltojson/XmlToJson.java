package xmltojson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class XmlToJson {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        File arquivo = new File("Entrada.xml");
        Queue<String> linhas = new LinkedList<>();

        if (arquivo.exists()) {
            try (Scanner scanner = new Scanner(arquivo)) {
                while (scanner.hasNext()) {
                    linhas.add(scanner.nextLine());
                }
            }

            String Json = converteParaJson(linhas);
            
            try
            {
                arquivo = new File("Saida.json");
                arquivo.createNewFile();
                try (FileWriter escritor = new FileWriter("Saida.json")) {
                    escritor.write(Json);
                }
                System.out.println("\nArquivo de saída criado!");
            }
            catch (IOException erro){
                System.out.println("Ocorreu um erro!");                
            }

        } else {
            System.out.println("Arquivo de entrada não encontrado. O arquivo de "
                    + "Entrada deve se chamar \'Entrada.xml\' e deve ser colocado "
                    + "na pasta raiz do projeto. Garanta que seu arquivo esteja "
                    + "bem formatado.");
        }
    }

    public static String converteParaJson(Queue<String> linhas) {
        String retorno = "";
        boolean elementos = false;
        boolean listando = false;
        boolean anteriorAbreTag = false;
        String tagAtual;
        String tagAnterior = "";
                
        retorno += "{";
        
        while (!linhas.isEmpty()) {
            String linha = linhas.poll();
            linha = linha.substring(1);
            if (linha.contains("<")) {
                //Tem abre e fecha tag
                if(elementos){
                    retorno += ",";
                }
                retorno += System.lineSeparator();
                elementos = true;
                retorno += "\"" + linha.substring(0, linha.indexOf('>')) + "\": \"" + 
                        linha.substring(linha.indexOf('>') + 1, linha.indexOf('<')) + 
                        "\"";
                anteriorAbreTag = false;
            } else if (linha.contains("/")) {
                //É um fecha tag
                elementos = false;
                tagAtual = linha.substring(1, linha.length() - 1);
                if (listando && !tagAnterior.equals(tagAtual)){
                    retorno += System.lineSeparator() + "]";
                    listando = false;
                }
                retorno += System.lineSeparator() + "}";
                anteriorAbreTag = false;
            } else {
                //É um abre tag
                elementos = false;                
                tagAtual = linha.substring(0, linha.length() - 1);
                if (tagAnterior.equals(tagAtual)){
                    retorno += "," + System.lineSeparator() + "{";
                }
                else {
                    if(!tagAnterior.equals("") && anteriorAbreTag){
                        int indice = retorno.lastIndexOf(tagAnterior) + tagAnterior.length() + 2;
                        retorno = retorno.substring(0, indice) + retorno.substring(indice + 2);
                        listando = false;
                    }
                    if (listando){
                        retorno += System.lineSeparator() + "]";
                    }
                    retorno += System.lineSeparator() + "\"" + tagAtual + "\": [ {";
                    listando = true;
                }
                tagAnterior = tagAtual;
                anteriorAbreTag = true;
            }
        }
        retorno += System.lineSeparator() + "}";
        
        return retorno;
    }
}
