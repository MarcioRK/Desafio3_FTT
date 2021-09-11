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
            System.out.println("Arquivo não existe. O arquivo criado de entrada deve ter o nome: \'Entrada.xml\' e tem que estar na pasta do projeto.");
        }
    }

    //Função responsável pela conversão de XML para JSON
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
            if (linha.contains("<")) {  //Possui tag de abertura e fechamento
                //A variavel 'letras' é true quando é o fim de um array ou item de um array e possui outro após este
                if(letras){
                    volta += ",";
                }

                //Pega a iformação até '>' e '<' e monta a estrutura aceita pelo JSON
                //Por possuir uma tag de abertura e fechamento é selecionado o nome do campo e seu valor
                volta += System.lineSeparator();
                letras = true;
                volta += "\"" + linha.substring(0, linha.indexOf('>')) + "\": \"" + 
                        linha.substring(linha.indexOf('>') + 1, linha.indexOf('<')) + 
                        "\"";
                 tagAberta = false;
            } else if (linha.contains("/")) {   //É uma tag de fechamento
                letras = false;
                tagNova = linha.substring(1, linha.length() - 1);
                //Por ser apenas a tag de fechamento só é adicionado ']' para fechar o objeto no JSON 
                if (lista && !tagAntiga.equals(tagNova)){
                    volta += System.lineSeparator() + "]";
                    lista = false;
                }
                volta += System.lineSeparator() + "}";
                tagAberta = false;
            } else { //É uma tag de abertura
                letras = false;                
                tagNova = linha.substring(0, linha.length() - 1);
                //Abertura de um novo array
                if (tagAntiga.equals(tagNova)){
                    volta += "," + System.lineSeparator() + "{";
                }
                else {
                    if(!tagAntiga.equals("") && tagAberta){
                        int indice = volta.lastIndexOf(tagAntiga) + tagAntiga.length() + 2;
                        volta = volta.substring(0, indice) + volta.substring(indice + 2);
                        lista = false;
                    }
                    //Fim do objeto
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
