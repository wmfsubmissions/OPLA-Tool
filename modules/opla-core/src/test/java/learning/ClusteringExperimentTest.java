package learning;

import br.ufpr.dinf.gres.opla.entity.Objective;
import jmetal4.core.Solution;
import jmetal4.core.SolutionSet;
import liquibase.util.csv.CSVReader;
import org.junit.Test;
import utils.MathUtils;
import weka.core.EuclideanDistance;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClusteringExperimentTest {

    /* SCRIPTS R PARA EXPERIMENTOS
     BET
        Feature Driven, Class Coupling and Cohesion
        install.packages("scatterplot3d")
     x = c(1476.0,1895.0,1477.0,1471.0,1475.0,1477.0,1478.0,1471.0,1477.0,1471.0,1476.0,1471.0,1477.0,1477.0,1471.0,1478.0,1471.0,1478.0,1476.0,1478.0,1477.0,1470.0,1476.0,1476.0,1478.0,1471.0,1477.0,1477.0,1476.0,1478.0,1471.0,1476.0,1476.0,1475.0,1475.0,1476.0,1476.0,1478.0,1469.0,1469.0,1471.0,1477.0,1476.0,1470.0,1470.0,1476.0,1471.0,1477.0,1476.0,1478.0,1478.0,1477.0,1477.0,1476.0,1476.0,1470.0,1476.0,1477.0,1476.0,1477.0,1476.0,1475.0,1470.0,1475.0,1478.0,1478.0,1471.0,1469.0,1476.0,1477.0,1478.0,1477.0,1477.0,1476.0,1475.0,1476.0,1476.0,1478.0,1477.0,1471.0,1477.0,1478.0,1476.0,1477.0,1478.0,1477.0,1477.0,1470.0,1477.0,1476.0,1471.0,1478.0,1475.0,1476.0,1478.0,1470.0,1471.0,1475.0,1478.0,1477.0,1478.0,1471.0,1476.0,1477.0,1476.0,1474.0,1474.0,1477.0,1477.0,1477.0,1477.0,1476.0,1471.0,1478.0,1475.0,1476.0,1476.0,1476.0,1478.0,1485.0,1475.0,1476.0,1477.0,1477.0,1474.0,1471.0,1477.0,1475.0,1475.0,1475.0,1477.0,1476.0,1477.0,1477.0,1476.0,1469.0,1471.0,1478.0,1477.0,1477.0,1476.0,1496.0,1526.0,1503.0,1496.0,1508.0,1510.0,1537.0,1496.0,1513.0,1510.0,1527.0,1453.0,1504.0,1528.0,1417.0,1496.0,1560.0,1530.0,1498.0,1504.0,1510.0,1504.0,1563.0,1496.0,1504.0,1528.0,1561.0,1495.0,1545.0,1496.0,1504.0,1527.0,1560.0,1393.0,1508.0,1528.0,1510.0,1510.0,1510.0,1560.0,1504.0,1528.0,1510.0,1510.0,1528.0,1503.0,1508.0,1510.0,1507.0,1497.0,1504.0,1504.0,1560.0,1528.0,1528.0,1536.0,1504.0,1504.0,1496.0,1528.0,1527.0,1496.0,1528.0,1504.0,1495.0,1503.0,1573.0,1561.0,1508.0,1504.0,1504.0,1510.0,1536.0,1503.0,1510.0,1494.0,1563.0,1566.0,1560.0,1510.0,1504.0,1496.0,1573.0,1528.0,1536.0,1561.0,1497.0,1528.0,1528.0,1504.0,1417.0,1496.0,1504.0,1504.0,1510.0,1494.0,1504.0,1504.0,1504.0,1510.0,1560.0,1510.0,1510.0,1560.0,1507.0,1496.0,1528.0,1528.0,1496.0,1508.0,1573.0,1504.0,1518.0,1528.0,1461.0,1502.0,1510.0,1528.0,1502.0,1504.0,1504.0,1393.0,1428.0,1504.0,1502.0,1560.0,1519.0,1563.0,1528.0,1509.0,1510.0,1526.0,1485.0,1504.0,1510.0,1508.0,1504.0,1504.0,1498.0,1504.0,1508.0,1560.0,1528.0,1503.0,1528.0,1498.0,1510.0,1528.0,1563.0,1497.0,1536.0,1528.0,1504.0,1573.0,1485.0,1506.0,1506.0,1526.0,1504.0,1423.0,1573.0,1504.0,1428.0,1560.0,1508.0,1428.0,1536.0,1508.0,1504.0,1510.0,1563.0,1417.0,1528.0,1528.0,1503.0,1563.0,1563.0,1508.0,1563.0,1498.0,1528.0,1498.0,1504.0,1510.0,1503.0)
     y = c(118.0,554.0,115.0,116.0,117.0,115.0,116.0,116.0,116.0,116.0,118.0,116.0,115.0,115.0,116.0,116.0,116.0,116.0,118.0,116.0,116.0,115.0,115.0,118.0,116.0,116.0,115.0,115.0,118.0,116.0,116.0,118.0,115.0,118.0,117.0,118.0,118.0,116.0,118.0,118.0,119.0,115.0,118.0,115.0,115.0,118.0,116.0,115.0,115.0,116.0,116.0,115.0,115.0,119.0,115.0,115.0,118.0,115.0,118.0,116.0,119.0,118.0,116.0,118.0,116.0,116.0,119.0,118.0,115.0,115.0,116.0,115.0,115.0,118.0,119.0,121.0,118.0,116.0,115.0,116.0,116.0,116.0,121.0,118.0,119.0,115.0,116.0,116.0,115.0,118.0,116.0,116.0,118.0,119.0,116.0,115.0,116.0,117.0,119.0,115.0,116.0,116.0,115.0,116.0,115.0,121.0,121.0,115.0,116.0,115.0,115.0,121.0,116.0,116.0,119.0,122.0,119.0,118.0,116.0,121.0,118.0,121.0,115.0,116.0,121.0,116.0,115.0,117.0,117.0,118.0,115.0,118.0,116.0,116.0,118.0,119.0,116.0,116.0,116.0,115.0,118.0,420.0,530.0,487.0,417.0,178.0,176.0,530.0,417.0,418.0,176.0,530.0,522.0,418.0,455.0,522.0,417.0,515.0,547.0,492.0,418.0,176.0,421.0,590.0,417.0,418.0,455.0,593.0,486.0,478.0,417.0,418.0,455.0,515.0,482.0,181.0,458.0,176.0,176.0,176.0,515.0,418.0,455.0,176.0,179.0,455.0,418.0,182.0,179.0,418.0,418.0,418.0,418.0,518.0,455.0,455.0,478.0,421.0,418.0,417.0,458.0,455.0,420.0,455.0,418.0,486.0,418.0,587.0,593.0,178.0,418.0,418.0,176.0,478.0,418.0,176.0,489.0,590.0,587.0,587.0,176.0,418.0,492.0,590.0,455.0,478.0,593.0,418.0,458.0,455.0,418.0,522.0,486.0,418.0,487.0,176.0,489.0,421.0,418.0,418.0,176.0,515.0,179.0,176.0,515.0,177.0,417.0,455.0,455.0,495.0,178.0,515.0,421.0,175.0,455.0,543.0,421.0,176.0,455.0,421.0,418.0,176.0,482.0,483.0,418.0,421.0,587.0,176.0,590.0,455.0,175.0,176.0,458.0,583.0,176.0,176.0,179.0,418.0,421.0,489.0,176.0,179.0,518.0,458.0,179.0,455.0,489.0,176.0,455.0,590.0,489.0,478.0,455.0,418.0,587.0,583.0,181.0,181.0,458.0,418.0,483.0,515.0,418.0,483.0,515.0,178.0,483.0,478.0,178.0,418.0,176.0,593.0,522.0,455.0,527.0,418.0,593.0,590.0,178.0,593.0,489.0,458.0,489.0,421.0,179.0,418.0)
     z = c(98.0,80.0,100.0,100.0,99.0,100.0,99.0,100.0,99.0,100.0,98.0,100.0,100.0,100.0,100.0,99.0,100.0,99.0,98.0,99.0,99.0,101.0,100.0,98.0,99.0,100.0,100.0,100.0,98.0,99.0,100.0,98.0,100.0,98.0,99.0,98.0,98.0,99.0,99.0,99.0,99.0,100.0,98.0,101.0,101.0,98.0,100.0,100.0,100.0,99.0,99.0,100.0,100.0,98.0,100.0,101.0,98.0,100.0,98.0,99.0,98.0,99.0,100.0,98.0,99.0,99.0,99.0,99.0,101.0,100.0,99.0,100.0,100.0,98.0,98.0,97.0,98.0,99.0,100.0,100.0,99.0,99.0,97.0,99.0,98.0,100.0,99.0,100.0,100.0,98.0,100.0,99.0,99.0,98.0,99.0,101.0,100.0,99.0,98.0,100.0,99.0,100.0,101.0,99.0,100.0,97.0,97.0,100.0,99.0,100.0,100.0,97.0,100.0,99.0,98.0,97.0,98.0,98.0,99.0,97.0,98.0,97.0,100.0,99.0,97.0,100.0,100.0,99.0,99.0,98.0,100.0,98.0,99.0,99.0,98.0,99.0,100.0,99.0,99.0,100.0,98.0,82.0,80.0,82.0,83.0,96.0,97.0,80.0,83.0,82.0,97.0,80.0,83.0,82.0,81.0,83.0,83.0,79.0,80.0,83.0,82.0,97.0,81.0,80.0,83.0,82.0,81.0,79.0,83.0,80.0,83.0,82.0,81.0,79.0,84.0,95.0,80.0,97.0,97.0,97.0,79.0,82.0,81.0,97.0,96.0,81.0,82.0,95.0,96.0,83.0,83.0,82.0,82.0,78.0,81.0,81.0,80.0,81.0,82.0,83.0,80.0,81.0,82.0,81.0,82.0,83.0,82.0,79.0,79.0,96.0,82.0,82.0,97.0,80.0,82.0,97.0,82.0,80.0,79.0,79.0,97.0,82.0,83.0,80.0,81.0,80.0,79.0,83.0,80.0,81.0,82.0,83.0,83.0,82.0,82.0,97.0,82.0,81.0,82.0,82.0,97.0,79.0,96.0,97.0,79.0,97.0,83.0,81.0,81.0,82.0,96.0,79.0,81.0,98.0,81.0,83.0,81.0,97.0,81.0,81.0,82.0,97.0,84.0,85.0,82.0,81.0,79.0,97.0,80.0,81.0,98.0,97.0,80.0,82.0,97.0,97.0,96.0,82.0,81.0,84.0,97.0,96.0,78.0,80.0,97.0,81.0,84.0,97.0,81.0,80.0,84.0,80.0,81.0,82.0,79.0,82.0,95.0,95.0,80.0,82.0,86.0,79.0,82.0,85.0,79.0,96.0,85.0,80.0,96.0,82.0,97.0,79.0,83.0,81.0,81.0,82.0,79.0,80.0,96.0,79.0,84.0,80.0,84.0,81.0,96.0,82.0)
     x = c(1476.0,1895.0,1477.0,1471.0,1475.0,1477.0,1478.0,1471.0,1477.0,1471.0,1476.0,1471.0,1477.0,1477.0,1471.0,1478.0,1471.0,1478.0,1476.0,1478.0,1477.0,1470.0,1476.0,1476.0,1478.0,1471.0,1477.0,1477.0,1476.0,1478.0,1471.0,1476.0,1476.0,1475.0,1475.0,1476.0,1476.0,1478.0,1469.0,1469.0,1471.0,1477.0,1476.0,1470.0,1470.0,1476.0,1471.0,1477.0,1476.0,1478.0,1478.0,1477.0,1477.0,1476.0,1476.0,1470.0,1476.0,1477.0,1476.0,1477.0,1476.0,1475.0,1470.0,1475.0,1478.0,1478.0,1471.0,1469.0,1476.0,1477.0,1478.0,1477.0,1477.0,1476.0,1475.0,1476.0,1476.0,1478.0,1477.0,1471.0,1477.0,1478.0,1476.0,1477.0,1478.0,1477.0,1477.0,1470.0,1477.0,1476.0,1471.0,1478.0,1475.0,1476.0,1478.0,1470.0,1471.0,1475.0,1478.0,1477.0,1478.0,1471.0,1476.0,1477.0,1476.0,1474.0,1474.0,1477.0,1477.0,1477.0,1477.0,1476.0,1471.0,1478.0,1475.0,1476.0,1476.0,1476.0,1478.0,1485.0,1475.0,1476.0,1477.0,1477.0,1474.0,1471.0,1477.0,1475.0,1475.0,1475.0,1477.0,1476.0,1477.0,1477.0,1476.0,1469.0,1471.0,1478.0,1477.0,1477.0,1476.0)
     y = c(118.0,554.0,115.0,116.0,117.0,115.0,116.0,116.0,116.0,116.0,118.0,116.0,115.0,115.0,116.0,116.0,116.0,116.0,118.0,116.0,116.0,115.0,115.0,118.0,116.0,116.0,115.0,115.0,118.0,116.0,116.0,118.0,115.0,118.0,117.0,118.0,118.0,116.0,118.0,118.0,119.0,115.0,118.0,115.0,115.0,118.0,116.0,115.0,115.0,116.0,116.0,115.0,115.0,119.0,115.0,115.0,118.0,115.0,118.0,116.0,119.0,118.0,116.0,118.0,116.0,116.0,119.0,118.0,115.0,115.0,116.0,115.0,115.0,118.0,119.0,121.0,118.0,116.0,115.0,116.0,116.0,116.0,121.0,118.0,119.0,115.0,116.0,116.0,115.0,118.0,116.0,116.0,118.0,119.0,116.0,115.0,116.0,117.0,119.0,115.0,116.0,116.0,115.0,116.0,115.0,121.0,121.0,115.0,116.0,115.0,115.0,121.0,116.0,116.0,119.0,122.0,119.0,118.0,116.0,121.0,118.0,121.0,115.0,116.0,121.0,116.0,115.0,117.0,117.0,118.0,115.0,118.0,116.0,116.0,118.0,119.0,116.0,116.0,116.0,115.0,118.0)
     z = c(98.0,80.0,100.0,100.0,99.0,100.0,99.0,100.0,99.0,100.0,98.0,100.0,100.0,100.0,100.0,99.0,100.0,99.0,98.0,99.0,99.0,101.0,100.0,98.0,99.0,100.0,100.0,100.0,98.0,99.0,100.0,98.0,100.0,98.0,99.0,98.0,98.0,99.0,99.0,99.0,99.0,100.0,98.0,101.0,101.0,98.0,100.0,100.0,100.0,99.0,99.0,100.0,100.0,98.0,100.0,101.0,98.0,100.0,98.0,99.0,98.0,99.0,100.0,98.0,99.0,99.0,99.0,99.0,101.0,100.0,99.0,100.0,100.0,98.0,98.0,97.0,98.0,99.0,100.0,100.0,99.0,99.0,97.0,99.0,98.0,100.0,99.0,100.0,100.0,98.0,100.0,99.0,99.0,98.0,99.0,101.0,100.0,99.0,98.0,100.0,99.0,100.0,101.0,99.0,100.0,97.0,97.0,100.0,99.0,100.0,100.0,97.0,100.0,99.0,98.0,97.0,98.0,98.0,99.0,97.0,98.0,97.0,100.0,99.0,97.0,100.0,100.0,99.0,99.0,98.0,100.0,98.0,99.0,99.0,98.0,99.0,100.0,99.0,99.0,100.0,98.0)
     s3d = scatterplot3d(x, y, z, type = "p", color = "blue", angle = 10, pch = 1, main = "scatterplot3d", zlab="Feature Driven", ylab="ACLASS", xlab="COE")
     semcluster = c(0.407209, 0.323401, 0.386058, 0.388186, 0.372919, 0.353859,     0.373962, 0.379551, 0.355527, 0.400275, 0.362495, 0.382301,     0.371036, 0.386933, 0.361358, 0.375651, 0.362005, 0.367474,     0.396773, 0.353165, 0.352628, 0.413356, 0.353165, 0.368163,     0.417212, 0.365668, 0.374662, 0.394289, 0.367543, 0.371868)
     kmeans = c(0.010201,0.010201,0.010201,0.010201,1.030301,1.030301,1.030301,0.010201,0.010201)
     dbscan = c(0.152355, 0.000101, 0.057684, 0.058372, 0.105379, 0.057684,     0.104871, 0.058372, 0.105938, 0.152355, 0.058372, 0.057684,     0.057684, 0.058372, 0.104871, 0.058372, 0.104871, 0.152355,     0.104871, 0.10512, 0.010177, 0.057821, 0.152355, 0.104871,     0.058372, 0.057684, 0.057684, 0.152355, 0.104871, 0.058372,     0.152355, 0.057821, 0.152954, 0.152355, 0.152355, 0.104871,     0.106627, 0.106627, 0.10589, 0.057684, 0.152355, 0.010177,     0.010177, 0.152355, 0.058372, 0.057684, 0.057821, 0.104871,     0.104871, 0.057684, 0.057684, 0.152009, 0.057821, 0.010177,     0.152355, 0.057684, 0.152355, 0.10512, 0.152009, 0.10514,     0.058508, 0.152715, 0.104871, 0.104871, 0.10589, 0.106627,     0.010035, 0.057684, 0.104871, 0.057684, 0.057684, 0.152355,     0.152368, 0.199494, 0.104871, 0.057684, 0.058372, 0.10512,     0.104871, 0.198456, 0.104644, 0.151291, 0.057684, 0.10512,     0.058638, 0.152355, 0.058372, 0.104871, 0.10514, 0.152009,     0.104871, 0.010177, 0.058372, 0.105379, 0.151291, 0.057684,     0.104871, 0.058372, 0.010035, 0.10512, 0.057821, 0.199394,     0.199394, 0.057684, 0.10512, 0.057684, 0.057684, 0.198456,     0.058372, 0.104871, 0.152368, 0.198003, 0.152009, 0.152355,     0.104871, 0.194236, 0.152715, 0.198456, 0.057684, 0.10512,     0.199394, 0.058372, 0.057684, 0.105379, 0.105379, 0.152715,     0.057684, 0.152355, 0.10512, 0.10512, 0.152355, 0.106385,     0.058372, 0.104871, 0.10512, 0.057684, 0.152355)
     shapiro.test(semcluster)
     shapiro.test(kmeans)
     shapiro.test(dbscan)
    */

    @Test
    public void agmOnDBSCAN() throws Exception {
        List<Objective> objectives = getObjectivesFromFile("agm_objectives_27112018.csv");
        SolutionSet solutionSet = getSolutionSetFromObjectiveList(objectives);

        Clustering clustering = new Clustering(solutionSet, ClusteringAlgorithm.DBSCAN);
        SolutionSet run = clustering.run();


        System.out.println("Clustering PLAS");
        run.getSolutionSet().forEach(solution -> {
            System.out.println(solution.getSolutionName() + " - " + solution);
        });
    }

    @Test
    public void agmOnKMeans() throws Exception {
        List<Objective> objectives = getObjectivesFromFile("agm_objectives_27112018.csv");
        SolutionSet solutionSet = getSolutionSetFromObjectiveList(objectives);

        Clustering clustering = new Clustering(solutionSet, ClusteringAlgorithm.KMEANS);
        EuclideanDistance distance = new EuclideanDistance();
        distance.setDontNormalize(true);
        clustering.setDistanceFunction(distance);
        SolutionSet run = clustering.run();

        System.out.println("Clustering PLAS");
        run.getSolutionSet().forEach(solution -> {
            System.out.println(solution.getSolutionName() + " - " + solution);
        });
    }

    @Test
    public void betOnDBSCAN() throws Exception {
        List<Objective> objectives = getObjectivesFromFile("bet_objectives_02122018.csv");
        SolutionSet solutionSet = getSolutionSetFromObjectiveList(objectives);
        Clustering clustering = new Clustering(solutionSet, ClusteringAlgorithm.DBSCAN);
        SolutionSet run = clustering.run();

        System.out.println("Clustering PLAS");
        run.getSolutionSet().forEach(solution -> {
//            System.out.println(solution.getSolutionName() + " - " + solution);
            System.out.print(solution.toString().split(" ")[2] + ",");
        });

        List<List<Double>> result = MathUtils.normalizeWithAllSolutions(run);


        Long lastExec = null;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            builder.append(result.get(i).toString().replace("[", "").replace("]", "").replaceAll(",", ""));
            if (!run.getSolutionSet().get(i).getExecutionId().equals(lastExec)) {
                lastExec = run.getSolutionSet().get(i).getExecutionId();
                builder.append("");
            }
        }


    }


    @Test
    public void betOnKMeans() throws Exception {
        List<Objective> objectives = getObjectivesFromFile("bet_objectives_02122018.csv");
        SolutionSet solutionSet = getSolutionSetFromObjectiveList(objectives);

        Clustering clustering = new Clustering(solutionSet, ClusteringAlgorithm.KMEANS);
        SolutionSet run = clustering.run();

        System.out.println("Clustering PLAS");
        run.getSolutionSet().forEach(solution -> {
            System.out.println(solution.getSolutionName() + " - " + solution);
        });
        List<List<Double>> result = MathUtils.normalize(run);
        Long lastExec = null;
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i).toString().replace("[", "").replace("]", "").replaceAll(",", ""));
            if (!run.getSolutionSet().get(i).getExecutionId().equals(lastExec)) {
                lastExec = run.getSolutionSet().get(i).getExecutionId();
                System.out.println("");
            }
        }

        System.out.println(result);

    }

    private SolutionSet getSolutionSetFromObjectiveList(List<Objective> objectives) {
        SolutionSet solutionSet = new SolutionSet();
        objectives.forEach(objective -> {
            Solution solution = new Solution();
            solution.createObjective(3);
            String[] split = objective.getObjectives().split("\\|");
            solution.setObjective(0, Double.parseDouble(split[0]));
            solution.setObjective(1, Double.parseDouble(split[1]));
            solution.setObjective(2, Double.parseDouble(split[2]));
            solution.setSolutionName(objective.getSolutionName());
            solution.setExecutionId(objective.getExecution() != null ? objective.getExecution().getId() : 0);
            solution.setNumberOfObjectives(3);
            solutionSet.getSolutionSet().add(solution);
        });
        return solutionSet;
    }

    private List<Objective> getObjectivesFromFile(String filename) throws IOException {
        File file = new File(Thread.currentThread().getContextClassLoader().getResource(filename).getFile());
        CSVReader reader = new CSVReader(new FileReader(file));
        String[] nextLine;
        List<Objective> objectives = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null) {
            objectives.add(new Objective(nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5]));
        }
        return objectives;
    }
}
