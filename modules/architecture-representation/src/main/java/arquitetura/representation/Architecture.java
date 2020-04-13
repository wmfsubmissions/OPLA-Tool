package arquitetura.representation;

import arquitetura.exceptions.ClassNotFound;
import arquitetura.flyweights.VariabilityFlyweight;
import arquitetura.flyweights.VariantFlyweight;
import arquitetura.flyweights.VariationPointFlyweight;
import arquitetura.helpers.UtilResources;
import arquitetura.main.GenerateArchitecture;
import arquitetura.representation.relationship.*;
import com.rits.cloning.Cloner;
import jmetal4.core.Variable;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author edipofederle<edipofederle               @               gmail.com>
 */
public class Architecture extends Variable {

    private static Logger LOGGER = Logger.getLogger(Architecture.class);
    private static final long serialVersionUID = -7764906574709840088L;
    public static String ARCHITECTURE_TYPE = "arquitetura.representation.Architecture";
    private Cloner cloner;
    private Set<Package> packages = new HashSet<Package>();
    private Set<Class> classes = new HashSet<Class>();
    private Set<Interface> interfaces = new HashSet<Interface>();
    private String name;
    private boolean appliedPatterns;

    private RelationshipsHolder relationshipHolder = new RelationshipsHolder();

    // tiago madrigar
    public int linkOverloadViolation =0; /// quantidade de elementos que com link overload (quantidade de anomalia detectada)
    public int excedLink = 0; /// somatótia da quantidade de ligações que excedem o thz (se thz for 2 e tiver 5 ligações de entrada, o excesso é 3)


    public Architecture(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public List<Element> getElements() {
        final List<Element> elts = new ArrayList<Element>();

        for (Package p : getAllPackages())
            for (Element element : p.getElements())
                elts.add(element);

        for (Class c : this.classes)
            elts.add(c);
        for (Interface i : this.interfaces)
            elts.add(i);

        return elts;
    }

    /**
     * Retorna um Map imutável. É feito isso para garantir que nenhum modificação seja
     * feita diretamente na lista
     *
     * @return Map<String               ,                               Concern>
     */
    public List<Concern> getAllConcerns() {
        final List<Concern> concerns = new ArrayList<Concern>();
        for (Map.Entry<String, Concern> entry : ConcernHolder.INSTANCE.getConcerns().entrySet()) {
            concerns.add(entry.getValue());
        }
        return concerns;
    }

    /**
     * Retorna um Map imutável. É feito isso para garantir que nenhum modificação seja
     * feita diretamente na lista
     * <p>
     * Set<Package>
     *
     * @return Set<Package>
     */
    public Set<Package> getAllPackages() {
        return Collections.unmodifiableSet(this.packages);
    }

    /**
     * Retorna interfaces que não tem nenhum pacote.
     * <p>
     * Retorna um Set imutável. É feito isso para garantir que nenhum modificação seja
     * feita diretamente na lista.
     *
     * @return Set<Class>
     */
    public Set<Interface> getInterfaces() {
        return Collections.unmodifiableSet(this.interfaces);
    }

    /**
     * Retorna todas as interfaces que existem na arquiteutra.
     * Este método faz um merge de todas as interfaces de todos os pacotes + as interfaces que não tem pacote
     *
     * @return
     */
    public Set<Interface> getAllInterfaces() {
        final Set<Interface> interfaces = new HashSet<Interface>();
        for (Package p : this.packages)
            interfaces.addAll(p.getAllInterfaces());

        interfaces.addAll(this.interfaces);
        return Collections.unmodifiableSet(interfaces);
    }

    /**
     * Retorna classes que não tem nenhum pacote.
     * <p>
     * Retorna um Set imutável. É feito isso para garantir que nenhum modificação seja
     * feita diretamente na lista
     *
     * @return Set<Class>
     */
    public Set<Class> getClasses() {
        return Collections.unmodifiableSet(this.classes);
    }

    /**
     * Retorna todas as classes que existem na arquiteutra.
     * Este método faz um merge de todas as classes de todos os pacotes + as classes que não tem pacote
     *
     * @return
     */
    public Set<Class> getAllClasses() {
        final Set<Class> klasses = new HashSet<Class>();
        for (Package p : this.packages)
            klasses.addAll(p.getAllClasses());

        klasses.addAll(this.classes);
        return Collections.unmodifiableSet(klasses);

    }

    /**
     * Busca elemento por nome.<br/>
     * <p>
     * No momento busca por class, interface ou package <br/>
     * <p>
     * <p>
     * TODO refatorar para buscar todo tipo de elemento
     *
     * @param name - Nome do elemento
     * @return
     * @parm type - tipo do elemento (class, interface ou package)
     */
    public Element findElementByName(String name, String type) {
        return findElement(name, type);
    }

    private Element findElement(String name, String type) {
        if (type.equalsIgnoreCase("class")) {
            for (Element element : getClasses()) {
                if (element.getName().equalsIgnoreCase(name))
                    return element;
            }
            for (Package p : getAllPackages()) {
                for (Element element : p.getElements()) {
                    if (element.getName().equalsIgnoreCase(name))
                        return element;
                }
            }
        }

        if (type.equalsIgnoreCase("interface")) {
            for (Element element : getInterfaces()) {
                if (element.getName().equalsIgnoreCase(name))
                    return element;
            }

            for (Package p : getAllPackages()) {
                for (Element element : p.getElements()) {
                    if (element.getName().equalsIgnoreCase(name))
                        return element;
                }
            }
        }

        if (type.equalsIgnoreCase("package")) {
            for (Element element : getAllPackages())
                if (element.getName().equalsIgnoreCase(name))
                    return element;
        }
        return null;
    }


    /**
     * Recupera uma classe por nome.
     *
     * @param className
     * @return {@link Class}
     */
    public List<Class> findClassByName(String className) {
        List<Class> classesFound = new ArrayList<Class>();
        for (Class klass : getClasses())
            if (className.trim().equalsIgnoreCase(klass.getName().trim()))
                classesFound.add(klass);

        for (Package p : this.packages)
            for (Class klass : p.getAllClasses())
                if (className.trim().equalsIgnoreCase(klass.getName().trim()))
                    classesFound.add(klass);

        if (classesFound.isEmpty())
            return null;
        return classesFound;
    }

    /**
     * Busca elemento por nome.
     *
     * @param elementName
     * @return - null se nao encontrar
     */
    public Element findElementByName(String elementName) {
        Element element = searchRecursivellyInPackage(this.packages, elementName);
        if (element == null) {
            for (Class klass : this.classes)
                if (klass.getName().equals(elementName))
                    return klass;
            for (Interface inter : this.interfaces)
                if (inter.getName().equals(elementName))
                    return inter;
        }
        if (element == null)
            LOGGER.info("No element called: " + elementName + " found");
        return element;
    }

    private Element searchRecursivellyInPackage(Set<Package> packages, String elementName) {
        for (Package p : packages) {
            for (Element element : p.getElements()) {
                if (element.getName().equals(elementName))
                    return element;
                searchRecursivellyInPackage(p.getNestedPackages(), elementName);
            }

            if (p.getName().equals(elementName))
                return p;
            searchRecursivellyInPackage(p.getNestedPackages(), elementName);
        }

        return null;
    }

    public Interface findInterfaceByName(String interfaceName) {
        for (Interface interfacee : getInterfaces())
            if (interfaceName.equalsIgnoreCase(interfacee.getName()))
                return interfacee;
        for (Package p : this.packages)
            for (Interface interfacee : p.getAllInterfaces())
                if (interfaceName.equalsIgnoreCase(interfacee.getName()))
                    return interfacee;

        return null;
    }

    /**
     * Busca um pacote por nome.
     *
     * @param packageName
     * @return Package
     * @throws return null caso pacote não existir.
     */
    public Package findPackageByName(String packageName) {
        for (Package pkg : getAllPackages())
            if (packageName.equalsIgnoreCase(pkg.getName()))
                return pkg;

        return null;
    }


    public Package createPackage(String packageName) {
        Package pkg = new Package(getRelationshipHolder(), packageName);
        this.packages.add(pkg);
        return pkg;
    }

    public Package createPackage(String packageName, String id) {
        Package pkg = new Package(getRelationshipHolder(), packageName, id);
        this.packages.add(pkg);
        return pkg;
    }

    public void removePackage(Package p) {
        /**
         * Remove qualquer relacionamento que os elementos do pacote
         * que esta sendo deletado possa ter.
         */
        for (Element element : p.getElements()) {
            relationshipHolder.removeRelatedRelationships(element);
        }
        //Remove os relacionamentos que o pacote possa pertencer
        relationshipHolder.removeRelatedRelationships(p);

        this.packages.remove(p);
        LOGGER.info("Pacote:" + p.getName() + "removido");
    }

    public Interface createInterface(String interfaceName) {
        Interface interfacee = new Interface(getRelationshipHolder(), interfaceName);
        this.addExternalInterface(interfacee);
        return interfacee;
    }

    public Interface createInterface(String interfaceName, String id) {
        Interface interfacee = new Interface(getRelationshipHolder(), interfaceName, id);
        this.addExternalInterface(interfacee);
        return interfacee;
    }

    public Class createClass(String klassName, boolean isAbstract) {
        Class klass = new Class(getRelationshipHolder(), klassName, isAbstract);
        this.addExternalClass(klass);
        return klass;
    }

    public void removeInterface(Interface interfacee) {
        interfacee.removeInterfaceFromRequiredOrImplemented();
        relationshipHolder.removeRelatedRelationships(interfacee);
        if (removeInterfaceFromArch(interfacee)) {
            LOGGER.info("Interface:" + interfacee.getName() + " removida da arquitetura");
        }
    }


    private boolean removeInterfaceFromArch(Interface interfacee) {
        if (this.interfaces.remove(interfacee))
            return true;
        for (Package p : this.packages) {
            if (p.removeInterface(interfacee))
                return true;
        }
        return false;
    }

    public void removeClass(Element klass) {
        relationshipHolder.removeRelatedRelationships(klass);
        if (this.classes.remove(klass))
            LOGGER.info("Classe " + klass.getName() + "(" + klass.getId() + ") removida da arquitetura");

        for (Package pkg : this.getAllPackages()) {
            if (pkg.getAllClasses().contains(klass)) {
                if (pkg.removeClass(klass))
                    LOGGER.info("Classe " + klass.getName() + "(" + klass.getId() + ") removida da arquitetura. Pacote(" + pkg.getName() + ")");
            }
        }
    }

    public List<VariationPoint> getAllVariationPoints() {
        return VariationPointFlyweight.getInstance().getVariationPoints();
    }

    public List<Variant> getAllVariants() {
        return VariantFlyweight.getInstance().getVariants();
    }

    public List<Variability> getAllVariabilities() {
        return VariabilityFlyweight.getInstance().getVariabilities();
    }

    public Class findClassById(String idClass) throws ClassNotFound {
        for (Class klass : getClasses())
            if (idClass.equalsIgnoreCase(klass.getId().trim()))
                return klass;

        for (Package p : getAllPackages())
            for (Class klass : p.getAllClasses())
                if (idClass.equalsIgnoreCase(klass.getId().trim()))
                    return klass;

        throw new ClassNotFound("Class " + idClass + " can not found.\n");
    }

    public Interface findIntefaceById(String idClass) throws ClassNotFound {
        for (Interface klass : getInterfaces())
            if (idClass.equalsIgnoreCase(klass.getId().trim()))
                return klass;

        throw new ClassNotFound("Class " + idClass + " can not found.\n");
    }

    public void addExternalInterface(Interface interface_) {
        if (interfaces.add(interface_))
            LOGGER.info("Interface: " + interface_.getName() + " adicionada na arquiteutra");
        else
            LOGGER.info("TENTOU adicionar a interface : " + interface_.getName() + " na arquiteutra, porém não conseguiu");
    }

    /**
     * Retorna classe contendo método para manipular relacionamentos
     *
     * @return OperationsOverRelationships
     */
    public OperationsOverRelationships operationsOverRelationship() {
        return new OperationsOverRelationships(this);
    }

    public OperationsOverAssociation forAssociation() {
        return new OperationsOverAssociation(relationshipHolder);
    }

    public OperationsOverDependency forDependency() {
        return new OperationsOverDependency(relationshipHolder);
    }

    public void moveElementToPackage(Element klass, Package pkg) {
        if (pkg.getElements().contains(klass)) {
            return;
        }
        String oldPackageName = UtilResources.extractPackageName(klass.getNamespace());
        if (this.packages.contains(pkg)) {
            if (oldPackageName.equals("model")) {
                addClassOrInterface(klass, pkg);
                this.removeOnlyElement(klass);
            } else {
                Package oldPackage = this.findPackageByName(oldPackageName);
                if (oldPackage != null) {
                    addClassOrInterface(klass, pkg);
                    oldPackage.removeOnlyElement(klass);
                }
            }
        }
        klass.setNamespace(ArchitectureHolder.getName() + "::" + pkg.getName());
    }

    private void addClassOrInterface(Element klass, Package pkg) {
        if (klass instanceof Class) {
            pkg.addExternalClass((Class) klass);
        } else if (klass instanceof Interface) {
            pkg.addExternalInterface((Interface) klass);
        }
    }


    public OperationsOverGeneralization forGeneralization() {
        return new OperationsOverGeneralization(this);
    }


    public OperationsOverAbstraction forAbstraction() {
        return new OperationsOverAbstraction(this);
    }

    public boolean removeRelationship(Relationship as) {
        LOGGER.info("removeRelationship()");
        if (as == null) return false;
        if (relationshipHolder.removeRelationship(as)) {
            LOGGER.info("Relacionamento : " + as.getType() + " removido da arquitetura");
            return true;
        } else {
            LOGGER.info("TENTOU remover Relacionamento : " + as.getType() + " da arquitetura porém não consegiu");
            return false;
        }
    }

    public OperationsOverUsage forUsage() {
        return new OperationsOverUsage(this);
    }

    /**
     * Create an exact copy of the <code>Architecture</code> object.
     *
     * @return An exact copy of the object.
     */
    public Variable deepCopy() {
        return this.deepClone();
    }

    //private static int count = 1;
    public Architecture deepClone() {
        if (cloner == null)
            cloner = new Cloner();
        Architecture newArchitecture = cloner.deepClone(this);
        return newArchitecture;
    }


    public boolean addImplementedInterface(Interface supplier, Class client) {
        if (!haveRelationship(supplier, client)) {
            if (addRelationship(new RealizationRelationship(client, supplier, "", UtilResources.getRandonUUID()))) {
                LOGGER.info("ImplementedInterface: " + supplier.getName() + " adicionada na classe: " + client.getName());
                return true;
            } else {
                LOGGER.info("Tentou adicionar a interface " + supplier.getName() + " como interface implementada pela classe: " + client.getName());
                return false;
            }
        }
        return false;
    }

    private boolean haveRelationship(Interface supplier, Element client) {
        for (Relationship r : relationshipHolder.getAllRelationships()) {
            if (r instanceof RealizationRelationship)
                if (((RealizationRelationship) r).getClient().equals(client) && ((RealizationRelationship) r).getSupplier().equals(supplier))
                    return true;

            if (r instanceof DependencyRelationship)
                if (((DependencyRelationship) r).getClient().equals(client) && ((DependencyRelationship) r).getSupplier().equals(supplier))
                    return true;
        }
        return false;
    }

    public boolean addImplementedInterface(Interface supplier, Package client) {
        if (!haveRelationship(supplier, client)) {
            if (addRelationship(new RealizationRelationship(client, supplier, "", UtilResources.getRandonUUID()))) {
                LOGGER.info("ImplementedInterface: " + supplier.getName() + " adicionada ao pacote: " + client.getName());
                return true;
            } else {
                LOGGER.info("Tentou adicionar a interface " + supplier.getName() + " como interface implementada no pacote: " + client.getName());
                return false;
            }
        }
        return false;
    }

    public void removeImplementedInterface(Interface inter, Package pacote) {
        pacote.removeImplementedInterface(inter);
        relationshipHolder.removeRelatedRelationships(inter);
    }

    public void removeImplementedInterface(Class foo, Interface inter) {
        foo.removeImplementedInterface(inter);
        relationshipHolder.removeRelatedRelationships(inter);
    }

    public void addRequiredInterface(Interface supplier, Class client) {
        if (!haveRelationship(supplier, client)) {
            if (addRelationship(new DependencyRelationship(supplier, client, "", UtilResources.getRandonUUID())))
                LOGGER.info("RequiredInterface: " + supplier.getName() + " adicionada a: " + client.getName());
            else
                LOGGER.info("TENTOU adicionar RequiredInterface: " + supplier.getName() + " a : " + client.getName() + " porém não consegiu");
        }
    }

    public void addRequiredInterface(Interface supplier, Package client) {
        if (!haveRelationship(supplier, client)) {
            if (addRelationship(new DependencyRelationship(supplier, client, "", UtilResources.getRandonUUID())))
                LOGGER.info("RequiredInterface: " + supplier.getName() + " adicionada a: " + client.getName());
            else
                LOGGER.info("TENTOU adicionar RequiredInterface: " + supplier.getName() + " a : " + client.getName() + " porém não consegiu");
        }
    }

    public void deleteClassRelationships(Class class_) {
        Collection<Relationship> relationships = new ArrayList<Relationship>(class_.getRelationships());

        if (relationships != null) {
            for (Relationship relationship : relationships) {
                this.removeRelationship(relationship);
            }
        }
    }

    public boolean addRelationship(Relationship relationship) {
        if (!relationshipHolder.haveRelationship(relationship)) {
            if (relationshipHolder.addRelationship(relationship)) {
                LOGGER.info("Relacionamento: " + relationship.getType() + " adicionado na arquitetura.(" + UtilResources.detailLogRelationship(relationship) + ")");
                return true;
            } else {
                LOGGER.info("TENTOU adicionar Relacionamento: " + relationship.getType() + " na arquitetura porém não consegiu");
                return false;
            }
        }
        return false;
    }

    public Package findPackageOfClass(Class targetClass) {
        String packageName = UtilResources.extractPackageName(targetClass.getNamespace());
        return findPackageByName(packageName);
    }

    public void save(Architecture architecture, String pathToSave, String i) {
        GenerateArchitecture generate = new GenerateArchitecture();
        generate.generate(architecture, pathToSave + architecture.getName() + i);
    }

    /**
     * Procura um elemento por ID.<br>
     * Este método busca por elementos diretamente no primeiro nível da arquitetura (Ex: classes que não possuem pacotes)
     * , e também em pacotes.<br/><br/>
     *
     * @param xmiId
     * @return
     */
    public Element findElementById(String xmiId) {
        for (Class element : this.classes) {
            if (element.getId().equals(xmiId))
                return element;
        }
        for (Interface element : this.interfaces) {
            if (element.getId().equals(xmiId))
                return element;
        }
        for (Package p : getAllPackages()) {
            for (Element element : p.getElements()) {
                if (element.getId().equalsIgnoreCase(xmiId))
                    return element;
            }
        }

        for (Package p : getAllPackages()) {
            if (p.getId().equalsIgnoreCase(xmiId))
                return p;
        }

        return null;
    }

    /**
     * Adiciona um pacote na lista de pacotes
     *
     * @param {@link Package}
     */
    public void addPackage(arquitetura.representation.Package p) {
        if (this.packages.add(p))
            LOGGER.info("Pacote: " + p.getName() + " adicionado na arquitetura");
        else
            LOGGER.info("TENTOU adicionar o Pacote: " + p.getName() + " na arquitetura porém não consegiu");
    }

    /**
     * Adiciona uma classe na lista de classes.
     *
     * @param {@link Class}
     */
    public void addExternalClass(Class klass) {
        if (this.classes.add(klass))
            LOGGER.info("Classe: " + klass.getName() + " adicionado na arquitetura");
        else
            LOGGER.info("TENTOU adicionar a Classe: " + klass.getName() + " na arquitetura porém não consegiu");
    }

    public void removeRequiredInterface(Interface supplier, Package client) {
        if (!client.removeRequiredInterface(supplier)) ;
        relationshipHolder.removeRelatedRelationships(supplier);
    }

    public void removeRequiredInterface(Interface supplier, Class client) {
        if (!client.removeRequiredInterface(supplier)) ;
        relationshipHolder.removeRelatedRelationships(supplier);
    }

    public boolean removeOnlyElement(Element element) {
        if (element instanceof Class) {
            if (this.classes.remove(element)) {
                LOGGER.info("Classe: " + element.getName() + " removida do pacote: " + this.getName());
                return true;
            }
        } else if (element instanceof Interface) {
            if (this.interfaces.remove(element)) {
                LOGGER.info("Interface: " + element.getName() + " removida do pacote: " + this.getName());
                return true;
            }
        }

        return false;
    }

    public void setCloner(Cloner cloner) {
        this.cloner = cloner;
    }

    public RelationshipsHolder getRelationshipHolder() {
        return relationshipHolder;
    }

    public boolean isAppliedPatterns() {
        return appliedPatterns;
    }

    public void setAppliedPatterns(boolean b) {
        // TODO Auto-generated method stub
        this.appliedPatterns = b;
    }

    public void addAllClasses(Set<Class> classes) {
        this.classes.clear();
        this.classes.addAll(classes);
    }

    public void addAllInterfaces(Set<Interface> interfaces) {
        this.interfaces.clear();
        this.interfaces.addAll(interfaces);
    }

    @Override
    public String toString() {
        return "Architecture{" +
                "packages=" + packages +
                ", classes=" + classes +
                ", interfaces=" + interfaces +
                ", name='" + name + '\'' +
                ", appliedPatterns=" + appliedPatterns +
                ", relationshipHolder=" + relationshipHolder +
                '}';
    }

    public static Double median(List<Integer> values) {
        List<Integer> sortedValues = values.stream().sorted().collect(Collectors.toList());
        return sortedValues.size() % 2 == 0 ? (sortedValues.get((sortedValues.size() / 2) - 1) + sortedValues.get((sortedValues.size() / 2))) / 2 : Double.valueOf(sortedValues.get((int) Math.floor(sortedValues.size() / 2)));
    }

    public static Double mean(List<Integer> values) {
        return (double) (values.stream().mapToInt(Integer::intValue).sum() / values.size());
    }

    public static Double sum(List<Integer> values) {
        return (double) (values.stream().mapToInt(Integer::intValue).sum());
    }


    public String toDetailedString(boolean withAttrs) {


        List<Integer> qtdAtributosPorClasse = new ArrayList<>();
        List<Integer> qtdMetodosPorClasse = new ArrayList<>();
        int qtdClassesSemAttr = 0;

        for (Class aClass : getAllClasses()) {
            if (aClass.getAllAttributes().size() != 0)
                qtdAtributosPorClasse.add(aClass.getAllAttributes().size());
            else
                qtdClassesSemAttr++;
            if (aClass.getAllMethods().size() != 0)
                qtdMetodosPorClasse.add(aClass.getAllMethods().size());
        }


        StringBuilder str = new StringBuilder();
        str.append("Packages: " + getAllPackages() +
                ", qtdPackages: " + getAllPackages().size() +
                ", qtdClasses: " + getAllClasses().size() +
                ", qtdInterfaces: " + getAllInterfaces().size() +
                ", qtdA: " + qtdClassesSemAttr +
                ", \nqtdAggregation: " + getRelationshipHolder().getAllAgragations().size() +
                ", \ngetAllCompositions: " + getRelationshipHolder().getAllCompositions().size() +
                ", \ngetAllDependencies: " + getRelationshipHolder().getAllDependencies().size() +
                ", \ngetAllAssociations: " + getRelationshipHolder().getAllAssociations().size() +
                ", \ngetAllGeneralizations: " + getRelationshipHolder().getAllGeneralizations().size() +
                ", \ngetAllRelationships: " + getRelationshipHolder().getAllRelationships().size() +
                "\n");
        if (withAttrs) {
            str.append("    Classes: \n");
            str.append(getAllClasses().stream().map(clazz -> {
                return "       " + clazz.getName() +
                        " \n           qtdAttributes: " + clazz.getAllAttributes().size() +
                        " \n           qtdAbstractsMethods: " + clazz.getAllAbstractMethods().size() +
                        " \n           qtdConcerns: " + clazz.getAllConcerns().size() +
                        " \n           qtdAssociations: " + clazz.getAllAssociationClass().size() +
                        " \n           qtdMethods: " + clazz.getAllMethods().size() +
                        " \n           attributes: " + clazz.getAllAttributes().toString() +
                        " \n           methods: " + clazz.getAllMethods().toString() + "\n";
            }).collect(Collectors.joining()));
        }
        return str.toString();
    }


    // Tiago Calculo do LInk

    public strictfp Double getMedia(ArrayList<Integer> valor) { // calculo da media de uma lista
        try {
            return getSum(valor) / valor.size();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("The list has null values");
        }
    }

    public strictfp Double getSum(List<Integer> valor) { //calculo da soma
        Double soma = 0D;
        for (int i = 0; i < valor.size(); i++) {
            soma += valor.get(i);
        }
        return soma;
    }

    public strictfp Double getDesvioPadrao(ArrayList<Integer> valor) { //desvio padrao
        Double media = getMedia(valor);
        int tam = valor.size();
        Double desvPadrao = 0D;
        for (Integer vlr : valor) {
            Double aux = vlr - media;
            desvPadrao += aux * aux;
        }
        return Math.sqrt(desvPadrao / (tam - 1));
    }


    public ArrayList<Integer> getLinkOverload(Element element) { //funcao que incia uma lista vazia. pega o elemento e calcula o link desse elemento
        ArrayList<Integer> linkOverload = new ArrayList<>(); //lista vazia
        int entrada = 0;
        int saida = 0;
        int ambos = 0;

        if (element instanceof Class) { // se o elemento for do tiṕo classe // cast para o tipo classe
            Class class_ = (Class) element;
            for (Relationship r : class_.getRelationships()) { //pra cada relacionamento da classe verificar o tipo


                if (r instanceof DependencyRelationship) { //  se for do tipo DEPENDENCIA

                    DependencyRelationship dr = (DependencyRelationship) r; // castar

                    if (dr.getClient().getId().equals(class_.getId())) {   // verificar qual lado do relacionamento e
                        //elemento e o cliente
                        entrada++;
                    } else {
                        saida++;
                    }
                }
                if (r instanceof RealizationRelationship) { //  se for do tipo REALIZAÇÃO
                    RealizationRelationship dr = (RealizationRelationship) r; // castar
                    if (dr.getClient().getId().equals(class_.getId())) {   // verificar qual lado do relacionamento e
                        //elemento e o cliente
                        saida++;
                    } else {
                        //ELEMENTO E SUPPLIER
                        entrada++;
                    }
                }
                if (r instanceof AbstractionRelationship) { //  se for do tipo ABSTRAÇÃO
                    AbstractionRelationship dr = (AbstractionRelationship) r; // castar
                    if (dr.getClient().getId().equals(class_.getId())) {   // verificar qual lado do relacionamento e
                        //elemento e o cliente
                        saida++;
                    } else {
                        entrada++;
                    }
                }
                if (r instanceof AssociationRelationship) { //  se for do tipo ASSOCIAÇÃO
                    ambos++;
                }
                if (r instanceof AssociationClassRelationship) { //  se for do tipo dependencia
                    ambos++;
                }
            }
        }

        if (element instanceof Interface) { //mesma coisa para nternface
            Interface interface_ = (Interface) element; //cast
            for (Relationship r : interface_.getRelationships()) { //pega da interface e verifica
                if (r instanceof DependencyRelationship) { //  se for do tipo DEPENDENCIA
                    DependencyRelationship dr = (DependencyRelationship) r; // castar
                    if (dr.getClient().getId().equals(interface_.getId())) {   // verificar qual lado do relacionamento e
                        entrada++;
                    } else {
                        saida++;
                    }
                }
                if (r instanceof RealizationRelationship) { //  se for do tipo REALIZAÇÃO
                    RealizationRelationship dr = (RealizationRelationship) r; // castar
                    if (dr.getClient().getId().equals(interface_.getId())) {   // verificar qual lado do relacionamento e
                        //elemento e o cliente
                        saida++;
                    } else {
                        entrada++;
                    }
                }
                if (r instanceof AbstractionRelationship) { //  se for do tipo ABSTRAÇÃO
                    AbstractionRelationship dr = (AbstractionRelationship) r; // castar
                    if (dr.getClient().getId().equals(interface_.getId())) {   // verificar qual lado do relacionamento e
                        //elemento e o cliente
                        saida++;
                    } else {
                        entrada++;
                    }
                }
                if (r instanceof AssociationRelationship) { //  se for do tipo ASSOCIAÇÃO
                    ambos++;
                }
                if (r instanceof AssociationClassRelationship) { //  se for do tipo dependencia
                    ambos++;
                }
            }
        }

        linkOverload.add(entrada); // add os links de entrada
        linkOverload.add(saida);
        linkOverload.add(ambos);
        return linkOverload;   //retorno da funcao //retorna a lista com 3 posições
    }

    public ArrayList<Integer> getTHZLinkOverload() {         //calculo do thz
        ArrayList<Integer> linkOverload = new ArrayList<>();


        ArrayList<Integer> linkEntrada = new ArrayList<>(); // pega lista de entrada
        ArrayList<Integer> linkSaida = new ArrayList<>();
        ArrayList<Integer> linkAmbos = new ArrayList<>();

        ArrayList<Integer> listAux = new ArrayList<>(); // armazenar o linkoverload de cada elemento (classe, interface)
        for (Class clazz : this.getAllClasses()) { // cpra cada lasse existente

            listAux = getLinkOverload(clazz); //calculando o link overload da classe

            linkEntrada.add(listAux.get(0)); //add das 3 listas
            linkSaida.add(listAux.get(1));
            linkAmbos.add(listAux.get(2));
        }
        for (Interface interface_ : this.getAllInterfaces()) {
            listAux = getLinkOverload(interface_); //calculando o link overload da interface

            linkEntrada.add(listAux.get(0)); //add das 3 listas
            linkSaida.add(listAux.get(1));
            linkAmbos.add(listAux.get(2));
        }

        // 3 listas, entrada, saida e ambos
        // thz de cada uma das listas

        linkOverload.add(calculeThz(linkEntrada)); //calcula o thz de cada uma
        linkOverload.add(calculeThz(linkSaida));
        linkOverload.add(calculeThz(linkAmbos));


        return linkOverload; //retorna o thz das 3 listas
    }


    public int calculeThz(ArrayList<Integer> list) { //calculo que ja javia sido realisado para o concern overload
        Double media = 0.0;
        for (Integer n : list) {
            media += n;
            //System.out.println(n);
        }
        media = media / list.size();
        System.out.println(("media:") + media);


        // calculo do desvio padrao
        Double desvioPadrao = getDesvioPadrao(list);
        System.out.println(("desvio padrão:" + desvioPadrao));

        // media + desvio padrão
        Double THzb = media + desvioPadrao;
        // arredondar THzb para cima e retornar
        System.out.println("soma:"+THzb);
        return (int) Math.ceil(THzb);
    }

    public  void linkOverloadExist(ArrayList<Integer> thrz) { //verificar se existe linkoverload (anomalia)
        ArrayList<Element> DectecLink = new ArrayList();
        ArrayList<Integer> listAux = new ArrayList<>();
        for (Class class_ : this.getAllClasses()) { // classe
            listAux = getLinkOverload(class_); //verifica o link da classe

            if (listAux.get(0) > thrz.get(0)) { // compara com o thz calculado anterionmente
                DectecLink.add(class_);
                continue;
            }
            if (listAux.get(1) > thrz.get(1)) { //se for maior, tem anomalia // se for igual ainda é aceitavel
                DectecLink.add(class_);
                continue;
            }
            if (listAux.get(2) > thrz.get(2)) {
                DectecLink.add(class_);
                continue;
            }

        }
        for (Interface interface_ : this.getAllInterfaces()) { // verifica link na internface
            listAux = getLinkOverload(interface_);

            if (listAux.get(0) > thrz.get(0)) { // compara com o thz calculado anterionmente
                DectecLink.add(interface_);
                continue;
            }
            if (listAux.get(1) > thrz.get(1)) {
                DectecLink.add(interface_);
                continue;
            }
            if (listAux.get(2) > thrz.get(2)) {
                DectecLink.add(interface_);
                continue;
            }
        }

        linkOverloadViolation = DectecLink.size();

        DectecLink.clear();
        listAux.clear();

        linkOverloadExcedTHZ(thrz); // contar o total de relacionamentos que excede o thrz
        //return DectecLink; //se nao cair em cima, nao tem anomalia
    }

    public  void linkOverloadExcedTHZ(ArrayList<Integer> thrz) { //contagem excesso além do thz
        excedLink = 0;
        ArrayList<Integer> listAux = new ArrayList<>();
        for (Class class_ : this.getAllClasses()) { // classe
            listAux = getLinkOverload(class_); //verifica o link da classe

            if (listAux.get(0) > thrz.get(0)) { // compara com o thz calculado anterionmente
                excedLink += (listAux.get(0) - thrz.get(0));
            }
            if (listAux.get(1) > thrz.get(1)) { //se for maior, tem anomalia // se for igual ainda é aceitavel
                excedLink += (listAux.get(1) - thrz.get(1));
            }
            if (listAux.get(2) > thrz.get(2)) {
                excedLink += (listAux.get(2) - thrz.get(2));
            }

        }
        for (Interface interface_ : this.getAllInterfaces()) { // verifica link na internface
            listAux = getLinkOverload(interface_);

            if (listAux.get(0) > thrz.get(0)) { // compara com o thz calculado anterionmente
                excedLink += (listAux.get(0) - thrz.get(0));
            }
            if (listAux.get(1) > thrz.get(1)) {
                excedLink += (listAux.get(1) - thrz.get(1));
            }
            if (listAux.get(2) > thrz.get(2)) {
                excedLink += (listAux.get(2) - thrz.get(2));
            }
        }
        listAux.clear();
    }

}