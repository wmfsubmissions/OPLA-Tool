package com.ufpr.br.opla.configuration;

@Deprecated
public class DirTarget {

    private String directoryToSaveModels;
    private String directoryToInteraction;
    private String pathPapyrus;
    private String directoryToExportModels;
    private String pathToProfile;
    private String pathToProfileConcern;
    private String pathToTemplateModelsDirectory;
    private String pathToProfileRelationships;
    private String pathToProfilePatterns;

    /**
     * @return the directoryToSaveModels
     */
    public String getDirectoryToSaveModels() {
        return directoryToSaveModels;
    }

    /**
     * @param directoryToSaveModels the directoryToSaveModels to set
     */
    public void setDirectoryToSaveModels(String directoryToSaveModels) {
        this.directoryToSaveModels = directoryToSaveModels;
    }

    /**
     * @return the directoryToExportModels
     */
    public String getDirectoryToExportModels() {
        return directoryToExportModels;
    }

    /**
     * @param directoryToExportModels the directoryToExportModels to set
     */
    public void setDirectoryToExportModels(String directoryToExportModels) {
        this.directoryToExportModels = directoryToExportModels;
    }

    /**
     * @return the pathToProfile
     */
    public String getPathToProfile() {
        return pathToProfile;
    }

    /**
     * @param pathToProfile the pathToProfile to set
     */
    public void setPathToProfile(String pathToProfile) {
        this.pathToProfile = pathToProfile;
    }

    public String getPathToProfileConcern() {
        return pathToProfileConcern;
    }

    public void setPathToProfileConcern(String pathToProfileConcern) {
        this.pathToProfileConcern = pathToProfileConcern;
    }

    public String getPathToTemplateModelsDirectory() {
        return pathToTemplateModelsDirectory;
    }

    public void setPathToTemplateModelsDirectory(String pathToTemplateModelsDirectory) {
        this.pathToTemplateModelsDirectory = pathToTemplateModelsDirectory;
    }

    public String getPathToProfileRelationships() {
        return pathToProfileRelationships;
    }

    public void setPathToProfileRelationships(String pathToProfileRelationships) {
        this.pathToProfileRelationships = pathToProfileRelationships;
    }

    public String getPathToProfilePatterns() {
        return pathToProfilePatterns;
    }

    public void setPathToProfilePatterns(String pathToProfilePatterns) {
        this.pathToProfilePatterns = pathToProfilePatterns;
    }

    public String getDirectoryToInteraction() {
        return directoryToInteraction;
    }

    public void setDirectoryToInteraction(String directoryToInteraction) {
        this.directoryToInteraction = directoryToInteraction;
    }

    public String getPathPapyrus() {
        return pathPapyrus;
    }

    public void setPathPapyrus(String pathPapyrus) {
        this.pathPapyrus = pathPapyrus;
    }
}