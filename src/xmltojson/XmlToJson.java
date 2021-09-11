package xmltojson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class XmlToJson {

   
    public static void main(String[] args) throws FileNotFoundException {
        File arquivo = new File("Entrada.xml");
        Queue<String> linhas = new LinkedList<>();

        if (arquivo.exists()) {
            try (Scanner scanner = new Scanner(arquivo)) {
                while (scanner.hasNext()) {
                    linhas.add(scanner.nextLine());
                }
            }

            String Json = converteJson(linhas);
            
            try
            {
                arquivo = new File("Saida.json");
                arquivo.createNewFile();
                try (FileWriter escritor = new FileWriter("Saida.json")) {
                    escritor.write(Json);
                }
                System.out.println("\nArquivo Completo.");
            }
            catch (IOException erro){
                System.out.println("Erro!!!");                
            }

        } else {
            System.out.println("Arquivo não existe. O arquivo criado de "
                    + "Entrada deve ter o nome: \'Entrada.xml\' e deve ser alocado "
                    + "na pasta do projeto.");
        }
    }

    public static String converteJson(Queue<String> linhas) {
        
        boolean lista = false;
        boolean letras = false;
        boolean tagAberta = false;
        String volta = "";
        String tagNova;
        String tagAntiga = "";
                
        volta += "{";
        
        while (!linhas.isEmpty()) {
            String linha = linhas.poll();
            linha = linha.substring(1);
            if (linha.contains("<")) {
                if(letras){
                    volta += ",";
                }
                volta += System.lineSeparator();
                letras = true;
                volta += "\"" + linha.substring(0, linha.indexOf('>')) + "\": \"" + 
                        linha.substring(linha.indexOf('>') + 1, linha.indexOf('<')) + 
                        "\"";
                 tagAberta = false;
            } else if (linha.contains("/")) {
                letras = false;
                tagNova = linha.substring(1, linha.length() - 1);
                if (lista && !tagAntiga.equals(tagNova)){
                    volta += System.lineSeparator() + "]";
                    lista = false;
                }
                volta += System.lineSeparator() + "}";
                tagAberta = false;
            } else {
                letras = false;                
                tagNova = linha.substring(0, linha.length() - 1);
                if (tagAntiga.equals(tagNova)){
                    volta += "," + System.lineSeparator() + "{";
                }
                else {
                    if(!tagAntiga.equals("") && tagAberta){
                        int indice = volta.lastIndexOf(tagAntiga) + tagAntiga.length() + 2;
                        volta = volta.substring(0, indice) + volta.substring(indice + 2);
                        lista = false;
                    }
                    if (lista){
                        volta += System.lineSeparator() + "]";
                    }
                    volta += System.lineSeparator() + "\"" + tagNova + "\": [ {";
                    lista = true;
                }
                tagAntiga = tagNova;
                tagAberta = true;
            }
        }
        volta += System.lineSeparator() + "}";
        
        return volta;
    }
}
