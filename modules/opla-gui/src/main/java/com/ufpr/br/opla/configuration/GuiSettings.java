/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ufpr.br.opla.configuration;

/**
 * Classe para configurações relativas a GUI. Por enquanto somente o tamanho da fonte.
 *
 * @author elf
 */
public class GuiSettings {

    private int fontSize = 13;
    private String edChartType = "line";
    private String saveChartsAsPng = "true";

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getEdChartType() {
        return edChartType;
    }

    public void setEdChartType(String edChartType) {
        this.edChartType = edChartType;
    }

    public String getSaveChartsAsPng() {
        return this.saveChartsAsPng;
    }

    public void setSaveChartsAsPng(String saveChartsAsPng) {
        this.saveChartsAsPng = saveChartsAsPng;
    }


}
