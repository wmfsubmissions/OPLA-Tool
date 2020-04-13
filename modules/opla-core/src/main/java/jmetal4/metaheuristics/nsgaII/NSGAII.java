//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package jmetal4.metaheuristics.nsgaII;
import arquitetura.representation.*;
import arquitetura.representation.Class;
import arquitetura.representation.Package;
import jmetal4.core.*;
import jmetal4.problems.OPLA;
import jmetal4.qualityIndicator.QualityIndicator;
import jmetal4.util.Distance;
import jmetal4.util.JMException;
import jmetal4.util.Ranking;
import jmetal4.util.comparators.CrowdingComparator;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
/**
 * This class implements the NSGA-II algorithm.
 */
public class NSGAII extends Algorithm {
    private static final long serialVersionUID = 5815971727148859507L;
    private static final Logger LOGGER = Logger.getLogger(NSGAII.class);
    /**
     * Constructor
     *
     * @param problem Problem to solve
     */
    public NSGAII(Problem problem) {
        super(problem);
    } // NSGAII
    /**
     * Runs the NSGA-II algorithm.
     *
     * @return a <code>SolutionSet</code> that is a set of non dominated
     * solutions as a result of the algorithm execution
     * @throws JMException
     * @throws Exception
     */
    public SolutionSet execute() throws JMException {
        LOGGER.info("Iniciando Execução");
        int populationSize;
        int maxEvaluations;
        int evaluations;
        QualityIndicator indicators; // QualityIndicator object
        int requiredEvaluations; // Use in the example of use of the
        // indicators object (see below)
        SolutionSet population;
        SolutionSet offspringPopulation;
        SolutionSet union;
        Operator mutationOperator;
        Operator crossoverOperator;
        Operator selectionOperator;
        Distance distance = new Distance();
        // Read the parameters
        populationSize = ((Integer) getInputParameter("populationSize")).intValue();
        maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
        indicators = (QualityIndicator) getInputParameter("indicators");
        // Initialize the variables
        population = new SolutionSet(populationSize);
        evaluations = 0;
        requiredEvaluations = 0;
        // Read the operators
        mutationOperator = operators_.get("mutation");
        crossoverOperator = operators_.get("crossover");
        selectionOperator = operators_.get("selection");




        try {

            LOGGER.info("Calculando threashold CO");
            Solution newSolution1 = new Solution(problem_);
            mutationOperator.threshold = detectThreshold(newSolution1);
            LOGGER.info("threashold original CO: " + mutationOperator.threshold); //print do threashold


            LOGGER.info("Calculando threashold Large Class");
            mutationOperator.threshold_lc = detectThreshold_lc(newSolution1);
            LOGGER.info("threashold original large class: " + mutationOperator.threshold_lc); //print do threashold




            //calculo thrz linkoverload
            //LOGGER.info("Calculando threashold LinkOverload");

            /*
            ArrayList<Integer> linkOverloadThrz = ((Architecture)newSolution1.getDecisionVariables()[0]).getTHZLinkOverload(); // calcule o threashold
            LOGGER.info("threashold Link Overload original: " + linkOverloadThrz); //print do threashold


             */

            //ArrayList<Element> linkOverloadElements = ((Architecture)newSolution1.getDecisionVariables()[0]).linkOverloadExist(linkOverloadThrz); // verifique os elementos com anomalia na soluçao

            /*
            // mostrar os elementos e valores de link
            if(linkOverloadElements.size() > 0){
                for(Element element : linkOverloadElements){

                    ArrayList<Integer> linkOverloadValue = ((Architecture)newSolution1.getDecisionVariables()[0]).getLinkOverload(element); // calculo do link do elemento
                    System.out.println("Element: "+element.getName()+" - "+linkOverloadValue);
                }
            }
             */


            Thread.sleep(10000000);






            LOGGER.info("Criando População");
            // Create the initial solutionSet
            Solution newSolution;
            for (int i = 0; i < populationSize; i++) {
                newSolution = new Solution(problem_);
                // criar a diversidade na populacao inicial
                mutationOperator.execute(newSolution);
                // validar a solução, enquanto nao for uma solução válida, criar uma nova e avaliar de novo
                // while (!isVslid()
                //     criar nova solução
                while (!(isValidSolution( (Architecture) newSolution.getDecisionVariables()[0]))){
                    newSolution = new Solution(problem_);
                    // criar a diversidade na populacao inicial
                    mutationOperator.execute(newSolution);
                }
                problem_.evaluate(newSolution);
                problem_.evaluateConstraints(newSolution);
                evaluations++;
                population.add(newSolution);
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new JMException(e.getMessage());
        }
        try {
            LOGGER.info("Iniciando evoluções");

            Solution newSolution1 = new Solution(problem_);

            OPLA opla = (OPLA)problem_;

            //calculo thrz linkoverload
            LOGGER.info("Calculando threashold LinkOverload");

            ArrayList<Integer> linkOverloadThrz = ((Architecture)newSolution1.getDecisionVariables()[0]).getTHZLinkOverload(); // calcule o threashold
            LOGGER.info("threashold Link Overload original: " + linkOverloadThrz); //print do threashold

            // Generations
            while (evaluations < maxEvaluations) {
                // Create the offSpring solutionSet
                offspringPopulation = new SolutionSet(populationSize);
                Solution[] parents = new Solution[2];
                for (int i = 0; i < (populationSize / 2); i++) {
                    if (evaluations < maxEvaluations) {
                        LOGGER.info("Origin INDIVIDUO: " + i + " evolucao: " + evaluations);
                        parents[0] = (Solution) selectionOperator.execute(population);
                        parents[1] = (Solution) selectionOperator.execute(population);
                        Object execute = crossoverOperator.execute(parents);
                        if (execute instanceof Solution) {
                            Solution offSpring = (Solution) crossoverOperator.execute(parents);
                            if (isValidSolution((Architecture) offSpring.getDecisionVariables()[0])) {
                                problem_.evaluateConstraints(offSpring);
                                mutationOperator.execute(offSpring);
                                if (isValidSolution((Architecture) offSpring.getDecisionVariables()[0])) {

                                    // Verificar a quantidade de violações link overload
                                    ((Architecture)offSpring.getDecisionVariables()[0]).linkOverloadExist(linkOverloadThrz);

                                    problem_.evaluateConstraints(offSpring);


                                    // aplicar penalidade no fitness, caso link overload exista
                                    opla.evaluateLinkOverload(offSpring);


                                   // problem_.evaluate(offSpring);
                                    offspringPopulation.add(offSpring);
                                }
                                else{
                                    System.out.println("DESCARTADO");
                                    OPLA.contDiscardedSolutions_++;
                                }
                            }
                            else{
                                System.out.println("DESCARTADO");
                                OPLA.contDiscardedSolutions_++;
                            }
                        } else {
                            Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
                            if (isValidSolution((Architecture) offSpring[0].getDecisionVariables()[0])) {
                                problem_.evaluateConstraints(offSpring[0]);
                                mutationOperator.execute(offSpring[0]);
                                if (isValidSolution((Architecture) offSpring[0].getDecisionVariables()[0])) {
                                    problem_.evaluateConstraints(offSpring[0]);

                                    // Verificar a quantidade de violações link overload
                                    ((Architecture)offSpring[0].getDecisionVariables()[0]).linkOverloadExist(linkOverloadThrz);
                                    opla.evaluateLinkOverload(offSpring[0]);
                                    //problem_.evaluate(offSpring[0]);
                                    offspringPopulation.add(offSpring[0]);
                                }
                                else{
                                    System.out.println("DESCARTADO");
                                    OPLA.contDiscardedSolutions_++;
                                }
                            }
                            else{
                                System.out.println("DESCARTADO");
                                OPLA.contDiscardedSolutions_++;
                            }
                            if (isValidSolution((Architecture) offSpring[1].getDecisionVariables()[0])) {
                                problem_.evaluateConstraints(offSpring[1]);
                                mutationOperator.execute(offSpring[1]);
                                if (isValidSolution((Architecture) offSpring[1].getDecisionVariables()[0])) {
                                    problem_.evaluateConstraints(offSpring[1]);
                                    // Verificar a quantidade de violações link overload
                                    ((Architecture)offSpring[1].getDecisionVariables()[0]).linkOverloadExist(linkOverloadThrz);
                                    opla.evaluateLinkOverload(offSpring[1]);
                                    ///problem_.evaluate(offSpring[1]);
                                    offspringPopulation.add(offSpring[1]);
                                }
                                else{
                                    System.out.println("DESCARTADO");
                                    OPLA.contDiscardedSolutions_++;
                                }
                            }
                            else{
                                System.out.println("DESCARTADO");
                                OPLA.contDiscardedSolutions_++;
                            }
/*
                            Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
                            problem_.evaluateConstraints(offSpring[0]);
                            problem_.evaluateConstraints(offSpring[1]);
                            mutationOperator.execute(offSpring[0]);
                            mutationOperator.execute(offSpring[1]);
                            problem_.evaluateConstraints(offSpring[0]);
                            problem_.evaluateConstraints(offSpring[1]);
                            problem_.evaluate(offSpring[0]);
                            problem_.evaluate(offSpring[1]);
                            offspringPopulation.add(offSpring[0]);
                            offspringPopulation.add(offSpring[1]);
                            */
                        }
                        evaluations += 2;
                    }
                }
                System.out.println("DESCARTADOS:"+OPLA.contDiscardedSolutions_);
                // Create the solutionSet union of solutionSet and offSpring
                LOGGER.info("Union solutions");
                union = ((SolutionSet) population).union(offspringPopulation);

                // Ranking the union
                LOGGER.info("Ranking the union");
                Ranking ranking = new Ranking(union);

                //Thread.sleep(100000);

                int remain = populationSize;
                int index = 0;
                SolutionSet front = null;
                population.clear();
                // Obtain the next front
                LOGGER.info("getSubfront()");
                front = ranking.getSubfront(index);
                while ((remain > 0) && (remain >= front.size())) {
                    // Assign crowding distance to individuals
                    LOGGER.info("crowdingDistanceAssignment()");
                    distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
                    // Add the individuals of this front
                    for (int k = 0; k < front.size(); k++) {
                        population.add(front.get(k));
                    }
                    // Decrement remain
                    remain = remain - front.size();
                    // Obtain the next front
                    index++;
                    if (remain > 0) {
                        LOGGER.info("getSubfront()");
                        front = ranking.getSubfront(index);
                    }
                }
                // Remain is less than front(index).size, insert only the best
                // one
                if (remain > 0) { // front contains individuals to insert
                    LOGGER.info("crowdingDistanceAssignment()");
                    distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
                    front.sort(new CrowdingComparator());
                    for (int k = 0; k < remain; k++) {
                        population.add(front.get(k));
                    }
                    remain = 0;
                }

                union.clear();
                ranking.FunctionClear();
                front.clear();

                // This piece of code shows how to use the indicator object into
                // the code
                // of NSGA-II. In particular, it finds the number of evaluations
                // required
                // by the algorithm to obtain a Pareto front with a hypervolNSGAume
                // higher
                // than the hypervolume of the true Pareto front.
                if ((indicators != null) && (requiredEvaluations == 0)) {
                    double HV = indicators.getHypervolume(population);
                    if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
                        requiredEvaluations = evaluations;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new JMException(e.getMessage());
        }
        // Return as output parameter the required evaluations
        LOGGER.info("setOutputParameter()");
        setOutputParameter("evaluations", requiredEvaluations);
        // Return the first non-dominated front
        LOGGER.info("Ranking()");
        Ranking ranking = new Ranking(population);
        return ranking.getSubfront(0);
        // return population;
    } // execute


    public boolean isValidSolution(Architecture solution) {
        //return true;
        final List<Interface> allInterfaces = new ArrayList<Interface>(solution.getAllInterfaces());
        if (!allInterfaces.isEmpty()) {
            for (Interface itf : allInterfaces) {
                if ((itf.getImplementors().isEmpty()) && (itf.getDependents().isEmpty()) && (!itf.getOperations().isEmpty())) {
                    return false;
                }
            }
        }
        return true; //isValid;

    }


    //Calculat THzb
    // --------------------------------------------------------------------------
    public int detectThreshold(Solution solution) throws JMException {

        // quantidade de caracteristicas de cada classe e interface da solução
        ArrayList<Integer> lstConcernCount = new ArrayList<>();

        final Architecture arch = ((Architecture) solution.getDecisionVariables()[0]); // solução original

        // Lista de todos os pacotes
        final List<Package> allPackage = new ArrayList<Package>(arch.getAllPackages());
        if (!allPackage.isEmpty()) {

            for (Package selectedPackage : allPackage) { // para cada pacote da solução


                // verificar todas as classes do pacote
                List<Class> lstClass = new ArrayList<>(selectedPackage.getAllClasses());

                for (Class selectedClass:lstClass) { // para cada classe

                    // conta a quantidade de caracteristicas da classe e salva em lista
                    //lstConcernCount.add(selectedClass.getOwnConcerns().size());
                    lstConcernCount.add(selectedClass.getPriConcerns().size());

                    System.out.println("classe: "+selectedClass.getName()+" "+selectedClass.getPriConcerns().size());

                    //System.out.println("classe"+selectedClass.getAllConcerns().size()+selectedClass.getName());

                    /*
                    for(Concern s1 : selectedClass.getOwnConcerns()){
                        System.out.println(s1.getName());
                    }
                    */


                }

                // verificar todas as interfaces do pacote
                List<Interface> lstInterface = new ArrayList<>(selectedPackage.getAllInterfaces());

                for (Interface selectedInterface:lstInterface) {


                    // conta a quantidade de caracteristicas da interface e salva em lista
                    // lstConcernCount.add(selectedInterface.getOwnConcerns().size());
                    lstConcernCount.add(selectedInterface.getAllConcerns().size());
                    //System.out.println("interface"+selectedInterface.getAllConcerns().size());
                    System.out.println("interface: "+selectedInterface.getName()+" "+selectedInterface.getAllConcerns().size());

                }
            }

        }

        Double meanBrickConcerns = 0.0;
        for(Integer n:lstConcernCount){
            meanBrickConcerns += n;
            //System.out.println(n);
        }
        meanBrickConcerns = meanBrickConcerns/lstConcernCount.size();
        System.out.println(("media")+meanBrickConcerns);


        // calculo do desvio padrao
        Double stdDevOfBrickConcerns = getDesvioPadrao(lstConcernCount);
        System.out.println(("desvio padrão"+stdDevOfBrickConcerns));

        // media + desvio padrão
        Double THzb = meanBrickConcerns + stdDevOfBrickConcerns;
        // arredondar THzb para cima e retornar
        System.out.println("soma:"+THzb);
        return (int) Math.ceil(THzb);

    }


    public int detectThreshold_lc(Solution solution) throws JMException {

        // quantidade de atributos + metodos de cada classe da solução
        ArrayList<Integer> lstAtribMeth = new ArrayList<>();

        final Architecture arch = ((Architecture) solution.getDecisionVariables()[0]); // solução original

        // para cada classe contAtribMeth da arquitetura, contar a quantidade de metodos e atributos e adicionar a soma na lista lstAtriMeth
        for(Class contAtribMeth : arch.getAllClasses()){
            lstAtribMeth.add(contAtribMeth.getAllMethods().size()+ contAtribMeth.getAllAttributes().size());

        }
        Double meanBrickConcerns = 0.0;
        for(Integer n:lstAtribMeth){
            meanBrickConcerns += n;
            //System.out.println(n);
        }
        meanBrickConcerns = meanBrickConcerns/lstAtribMeth.size();
        System.out.println(("media")+meanBrickConcerns);


        // calculo do desvio padrao
        Double stdDevOfBrickConcerns = getDesvioPadrao(lstAtribMeth);
        System.out.println(("desvio padrão"+stdDevOfBrickConcerns));

        // media + desvio padrão
        Double THzb = meanBrickConcerns + stdDevOfBrickConcerns;
        // arredondar THzb para cima e retornar
        System.out.println("soma:"+THzb);
        return (int) Math.ceil(THzb);

    }





    public strictfp Double getMedia(ArrayList<Integer> valor) {
        try {
            return getSum(valor) / valor.size();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("The list has null values");
        }
    }

    public strictfp Double getSum(List<Integer> valor) {
        Double soma = 0D;
        for (int i = 0; i < valor.size(); i++) {
            soma += valor.get(i);
        }
        return soma;
    }

    public strictfp Double getDesvioPadrao(ArrayList<Integer> valor) {
        Double media = getMedia(valor);
        int tam = valor.size();
        Double desvPadrao = 0D;
        for (Integer vlr : valor) {
            Double aux = vlr - media;
            desvPadrao += aux * aux;
        }
        return Math.sqrt(desvPadrao / (tam - 1));
    }
} // NSGA-II