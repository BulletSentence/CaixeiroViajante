import java.io.*;
import java.util.Random;

public class CaixeiroViajante {

    static int totalCities, startCity;
    static int[][] dist_matrix;
    static int[][][] population;
    static String FILE = "problema1.txt";
    static int generation = 0;
    static int maxGeneration = 50;
    static int maxPopulation = 50;
    static double mutationRate = 0.5;
    static double crossoverRate = 0.5;
    static int[] ePopulation = new int[maxGeneration];
    static int elitism = 1;
    static double[][] fitness = new double[maxGeneration][maxPopulation];

    // Melhores resultados
    static int bestChromosome;
    static int bestGeneration;

    // Modelos de PRINT
    static boolean mostrar_adicionais = false;
    static boolean mostrar_matrix_dist = false;
    static boolean mostrar_detalhes_cromossomo_fitness = false;
    static boolean mostrar_passo_a_passo_cromossomo_fitness = false;
    static boolean mostrar_populacao = false;
    static boolean mostrar_eFitness = false;
    static boolean mostrar_mutacao = false;
    static boolean mostrar_dados = false;
    static boolean mostrar_geracoes = false;

    public static String converte(int letras) {
        String[] alfabeto = {"A", "B", "C", "D", "E", "F", "G", "H"};
        return alfabeto[letras].toString();
    }

    public CaixeiroViajante(String fileName){
        this.FILE = fileName;
    }

    // Inicia os valores de distancias
    public static void initialize_ds(){
        int city1,city2;
        BufferedReader readBuffer = null;
        String xValues[] = new String[totalCities];

        try{
            readBuffer = new BufferedReader(new FileReader(FILE));

            // Checa o numero de cidades
            String xString = readBuffer.readLine();
            int x = Integer.parseInt(xString);
            totalCities = x-1;
            dist_matrix = new int[totalCities+1][totalCities+1];

            // Checa a cidade de inicio
            xString = readBuffer.readLine();
            x = Integer.parseInt(xString);
            startCity = x-1;

            if(mostrar_adicionais == true)
                System.out.println("Total de Cidades: "+(totalCities+1)+" | Cidade Inicial: "+(startCity+1));

            // Pega a distancia entre as cidades
            city1 = 0;
            city2 = 1;

            // Percorre o arquivo CIDADES.TXT pegando as distancias
            for(int i = 0; i  < totalCities; i++){

                while(city1 != city2){

                    xString = readBuffer.readLine();
                    xValues = xString.split("\t");

                    for(int j = 0; j < xValues.length; j++){
                        x = Integer.parseInt(xValues[j]);
                        dist_matrix[city1][city2] = x;
                        if(city1 == 3 && city2 == 5)
                            dist_matrix[city2][city1] = x-10;
                        else
                            dist_matrix[city2][city1] = x;
                        if(mostrar_matrix_dist == true){
                            System.out.println(city2+","+city1+" : "+dist_matrix[city2][city1]);
                            System.out.println(city1+","+city2+" : "+dist_matrix[city1][city2]);
                        }
                        city1++;
                    }
                }
                city2++;
                city1 = 0;
            }

        }catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }
    }

    // Inicia os valores de população
    public static void initializePopulation(){
        int chromosome = 0;
        population = new int[maxGeneration][maxPopulation][totalCities+2];

        // Array de cidades visitadas
        int[] visitedCities = new int[totalCities+1];
        for(int i = 0; i < visitedCities.length; i++){
            visitedCities[i] = -1;
        }

        while(chromosome != maxPopulation){

            // Escolhe a cidade inicial (valor do gene) como primeira na lista
            population[generation][chromosome][0] = startCity;

            if(mostrar_populacao == true)
                System.out.print(generation+"-"+chromosome+" : "+population[generation][chromosome][0]);
            for(int gene = 1; gene <= totalCities; gene++){

                // gera um valor aleatório de gene (cidade)
                int city = genRandom(totalCities+1);

                // verifica se o valor já existe
                for(int i = 1; i < totalCities; i++){
                    if(city == visitedCities[i] || city == startCity){
                        city = genRandom(totalCities+1);
                        i = 0;
                    }
                }

                // Adiciona o valor ao cromossomo e às cidades visitadas
                population[generation][chromosome][gene] = city;
                visitedCities[gene] = city;

                if(mostrar_populacao == true)
                    System.out.print(" "+city+" ");
            }

            population[generation][chromosome][totalCities+1] = startCity;
            if(mostrar_populacao == true)
                System.out.println(" "+population[generation][chromosome][totalCities+1]);

            // cria outro cromossomo
            chromosome++;
        }
    }

    // Avalia a população pra caber no fitness
    public static void evaluatePopulation(){
        double totalDist = 0;
        double fitnessValue = 0;
        int cityA,cityB;
        int chromosome = 0;
        int eChromosome = 0;
        double eFitness = 0;

        while(chromosome != maxPopulation){

            for(int gene = 0; gene <= totalCities; gene++){
                // Pega o valor da cidade
                cityA = population[generation][chromosome][gene];
                // Pega o valor da proxima cidade
                cityB = population[generation][chromosome][gene+1];
                // Pega o valor da distancia delas e adiciona a distancia total
                totalDist += dist_matrix[cityA][cityB];
                if(mostrar_passo_a_passo_cromossomo_fitness == true)
                    System.out.println("step "+gene+"("+cityA+")"+"->"+(gene+1)+"("+cityB+")"+":"+totalDist);
            }

            // Calcula o valor do fitness entre 1 e 0
            fitnessValue = 1/totalDist;

            if( mostrar_detalhes_cromossomo_fitness == true){
                System.out.print(generation+"-"+chromosome+" | C:");
                for(int gene = 0 ; gene <= totalCities+1; gene++){
                    int var = population[generation][chromosome][gene];
                    System.out.print(" "+population[generation][chromosome][gene]+" ");

                }
                System.out.println("| D: "+totalDist+" | F: "+fitnessValue );
            }

//            if(fitnessValue == 0.00){
//                System.out.println("Bom");
//                System.exit(0);
//            }

            // Salva o valor do fitness
            fitness[generation][chromosome] += fitnessValue;
            if(fitnessValue > eFitness){
                eFitness = fitnessValue;
                eChromosome = chromosome;
            }

            // Vai pra proxima geração de cromossomo
            chromosome++;
            // Reseta os valores
            totalDist = 0;
        }

        if(mostrar_eFitness == true)
            System.out.println("Melhor Geração: "+generation+"-"+eChromosome+" : "+eFitness);

        // Adiciona a melhor geração para a ELITE
        ePopulation[generation]=eChromosome;

        if(generation == maxGeneration-1){
            // some print commands
            System.out.println("\nResultado:");
            // find the best stuff
            for(int i = 0; i < maxGeneration; i++){
                for(int j = 0; j < maxPopulation; j++){
                    if(fitness[i][j] > fitness[bestGeneration][bestChromosome]){
                        fitness[bestGeneration][bestChromosome] = fitness[i][j];
                        bestChromosome = j;
                        bestGeneration = i;
                    }
                }
            }

            System.out.print(bestGeneration+"-"+bestChromosome+" : SEQUENCIA: ");
            for(int gene = 0; gene <= totalCities+1; gene++){
                System.out.print(" "+converte(population[bestGeneration][bestChromosome][gene])+" ");
                // Pega a melhor distancia
                if(gene < totalCities+1){
                    cityA = population[bestGeneration][bestChromosome][gene];
                    cityB = population[bestGeneration][bestChromosome][gene+1];
                    totalDist += dist_matrix[cityA][cityB];
                }
            }
            if(mostrar_dados == true)
            System.out.print(" | D: "+totalDist+" | F: "+fitness[bestGeneration][bestChromosome]);
        }
    }

    // Cria a proxima geração com ajuda da antiga
    public static void createNextGen(){
        int elitismOffset = 0;
        int parentA,parentB;
        int [] usedGenes = new int[totalCities+2];

       // Se o Elitismo tiver TRUE o gene ELITE passa pra proxima geração
        if(elitism == 1){

            for(int chromosome = 0; chromosome < elitism; chromosome++){
                if(mostrar_populacao == true)
                    System.out.print(generation+"-"+chromosome+" :");
                for(int gene = 0; gene <= totalCities+1; gene++){
                    population[generation][chromosome][gene] = population[generation-1][ePopulation[generation-1]][gene];
                    if(mostrar_populacao == true)
                        System.out.print(" "+population[generation][chromosome][gene]+" ");
                }

                System.out.println();
            }

            elitismOffset++;
        }

        // Criando uma nova população
        for(int chromosome = elitismOffset; chromosome < maxPopulation; chromosome++){

            parentA = selectParent();
            parentB = selectParent();
            while(parentB == parentA){
                parentB = selectParent();
            }

            population[generation][chromosome][0] = startCity;
            population[generation][chromosome][totalCities+1] = startCity;
            if(mostrar_populacao == true)
                System.out.print(parentA+"+"+parentB+" | "+generation+"-"+chromosome+" : "+population[generation][chromosome][0]);

            // Cria um crossover entre genes
            for(int gene = 1; gene <= totalCities; gene++){

                // pai do gene escolhido randimicamente
                double pSelect = genRandomDouble();
                String sParent;

                // Se os genes forem iguais
                if(pSelect > crossoverRate){
                    population[generation][chromosome][gene] = population[generation-1][parentA][gene];
                    usedGenes[gene] = population[generation-1][parentA][gene];
                    sParent = "A";
                }else{
                    population[generation][chromosome][gene] = population[generation-1][parentB][gene];
                    sParent = "B";
                    usedGenes[gene] = population[generation-1][parentA][gene];
                }

                // Olha se o gene é repitido, se sim, gera outro aleatório
                for(int i = 1; i < gene; i++){
                    if(population[generation][chromosome][gene] == usedGenes[i] || population[generation][chromosome][gene] == startCity){
                        population[generation][chromosome][gene] = genRandom(totalCities+1);
                        sParent = "R";
                        i = 0;
                    }
                }

                usedGenes[gene] = population[generation][chromosome][gene];

                if(mostrar_populacao == true)
                    System.out.print(" "+population[generation][chromosome][gene]+"("+sParent+")"+" ");
            }

            if(mostrar_populacao == true)
                System.out.println(" "+startCity);
            if(mostrar_mutacao)
                System.out.print("M@");
            int gPosition = genRandom(totalCities)-1;
            if(mostrar_mutacao)
                System.out.print(gPosition+ " : "+population[generation][chromosome][0]);

            // MUTAÇÃO
            // A nossa mutação pega dois genes aleatorios e junta eles
            double doMutation = genRandomDouble();
            if(doMutation <= mutationRate){
                for(int gene = 1; gene <= totalCities; gene++){
                    int tGene;
                    if(gene == gPosition){
                        tGene = population[generation][chromosome][gene];
                        population[generation][chromosome][gene] = population[generation][chromosome][gene+1];
                        population[generation][chromosome][gene+1] = tGene;
                        if(mostrar_mutacao)
                            System.out.print("(S)");
                    }
                    if(mostrar_mutacao)
                        System.out.print(" "+population[generation][chromosome][gene]+" ");
                }
            }
            if(mostrar_mutacao)
                System.out.println(" "+population[generation][chromosome][totalCities+1]);
        }
    }

    private static int selectParent(){
        double totalFitness = 0.0;
        double random;
        int chromosome;
        double [][] parentChance = new double[2][maxPopulation];
        double pChance;

        for(chromosome = 0; chromosome < maxPopulation; chromosome++){
            totalFitness += fitness[generation-1][chromosome];
        }

        for(chromosome = 0; chromosome < maxPopulation; chromosome++){
            pChance = fitness[generation-1][chromosome]/totalFitness;
            parentChance[0][chromosome] = pChance;
            if(chromosome > 0){
                parentChance[1][chromosome] = parentChance[1][chromosome-1]+parentChance[0][chromosome-1];
            }else{
                parentChance[1][chromosome] = 0.0;
            }
        }
        random = genRandomDouble();

        for(chromosome = 0; chromosome < maxPopulation; chromosome++){
            if(random >= parentChance[0][chromosome] && random < (parentChance[1][chromosome]+parentChance[0][chromosome]))
                return chromosome;
        }

        return 0;
    }

    public static int genRandom(int x){
        Random output = new Random();
        int number = output.nextInt(x);
        return number;
    }

    // Gera um valor aleatorio double entre 1 e 0
    public static double genRandomDouble(){
        Random output = new Random();
        double number = output.nextDouble();
        return number;
    }

    public static void main(String[] args) {

        initialize_ds();

        if (mostrar_geracoes == true) {
            System.out.println("\nGeração: 0");
        }

        initializePopulation();
        evaluatePopulation();

        while(++generation < maxGeneration){
            createNextGen();
            if (mostrar_geracoes == true) {
                System.out.println("Geração: " + generation);
            }
            evaluatePopulation();
        }
    }
}