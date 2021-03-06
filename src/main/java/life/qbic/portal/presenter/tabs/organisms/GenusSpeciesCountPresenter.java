package life.qbic.portal.presenter.tabs.organisms;


import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import life.qbic.portal.exceptions.DataNotFoundException;
import life.qbic.portal.model.view.charts.PieChartModel;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.presenter.tabs.ATabPresenter;
import life.qbic.portal.presenter.utils.Colors;
import life.qbic.portal.presenter.utils.DataSorter;
import life.qbic.portal.presenter.utils.LabelFormatter;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.charts.PieView;
import submodule.data.ChartConfig;
import submodule.lexica.ChartNames;

import java.util.*;

/**
 * @author fhanssen
 */
public class GenusSpeciesCountPresenter extends ATabPresenter<PieChartModel, PieView> {

    private ChartConfig speciesConfig;
    private Map<String, List<String>> genusSpeciesMap;
    private ChartConfig genusConfig;
    private PlotOptionsPie innerPieOptions;
    private PlotOptionsPie outerPieOptions;
    private final String kingdom;


    public GenusSpeciesCountPresenter(MainPresenter mainPresenter, String kingdom) {
        super(mainPresenter, new PieView());
        this.kingdom = kingdom;
    }


    @Override
    protected void extractData() throws DataNotFoundException, NullPointerException {
        speciesConfig = super.getChartConfig(kingdom.concat("_Species"));
        genusConfig = super.getChartConfig(kingdom.concat("_Genus"));
        genusSpeciesMap = generateGenusToSpeciesMap(super.getChartConfig(ChartNames.Species_Genus.toString()));
    }

    private Map<String, List<String>> generateGenusToSpeciesMap(ChartConfig speciesGenusConfig) {
        Map<String, List<String>> result = new HashMap<>();

        Object[] objectArray = speciesGenusConfig.getData().keySet().toArray(new Object[0]);
        @SuppressWarnings("SuspiciousToArrayCall") String[] keySet = Arrays.asList(objectArray).toArray(new String[objectArray.length]);

        for (String aKeySet : keySet) {
            for (int i = 0; i < speciesGenusConfig.getData().get(aKeySet).size(); i++) {
                List<String> list = new ArrayList<>();
                //noinspection SuspiciousMethodCalls
                if (result.containsKey(speciesGenusConfig.getData().get(aKeySet).get(i))) {
                    //noinspection SuspiciousMethodCalls
                    list = result.get(speciesGenusConfig.getData().get(aKeySet).get(i));
                }
                list.add((String) speciesGenusConfig.getSettings().getxCategories().get(i));
                result.put((String) speciesGenusConfig.getData().get(aKeySet).get(i), list);
            }
        }

        return result;
    }

    @Override
    protected void addChartSettings() {
        innerPieOptions = new PlotOptionsPie();
        innerPieOptions.setSize("237px");
        innerPieOptions.setDataLabels(new DataLabels());
        innerPieOptions.getDataLabels().setFormatter("this.percentage >  10 ? this.point.name : null");
        innerPieOptions.getDataLabels().setColor(new SolidColor(255, 255, 255));
        innerPieOptions.getDataLabels().setDistance(-30);
        innerPieOptions.setAnimation(false);

        outerPieOptions = new PlotOptionsPie();
        outerPieOptions.setInnerSize("237px");
        outerPieOptions.setSize("318px");
        outerPieOptions.setDataLabels(new DataLabels());

        //This formats the labels for the outer pie. Unfortunately line breaks cannot be inserted in the label
        //itself, but have to be specified using javascript. Every 30 characters a line break is inserted, if a
        // gap exists in the word. Additionally, the subspec are written in italic.
        outerPieOptions.getDataLabels().setFormatter("function() {" +
                "var text = ''; " +
                "for (i = 0; i < 2; i++) {" +
                " text += ' ' + this.point.name.split(' ')[i] " +
                "} " +
                "for (i = 2; i < this.point.name.split(' ').length-1; i++) {" +
                " text += ' ' + this.point.name.split(' ')[i].italics() + ' ' " +
                "} " +
                "for (i = this.point.name.split(' ').length-1; i <  this.point.name.split(' ').length; i++) {" +
                " text += ' ' + this.point.name.split(' ')[i] " +
                "}" +
                "let wordsArr = text.split(' ')," +
                "wordsLen = wordsArr.length," +
                "finalMsg = ''," +
                "linesArr = ['']," +
                "currLine = 0;" +
                "for(let i=0; i< wordsLen; i++){" +
                "if(linesArr[currLine].length + wordsArr[i].length > 50){" + //50 chars is the longest line we allow right now
                "currLine +=1;" +
                "linesArr[currLine] = wordsArr[i] + ' ';" +
                "} else {" +
                "linesArr[currLine] += wordsArr[i] + ' ';" +
                "}" +
                "}" +
                "let linesLen = linesArr.length;" +
                "for(let i=0; i<linesLen; i++){" +
                "finalMsg += linesArr[i] + '<br>';" +
                "}" +
                "return finalMsg.trim();" +
                "}");
        outerPieOptions.setAnimation(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setValueSuffix("");

        this.getView().getConfiguration().addyAxis(new YAxis());

        this.setModel(new PieChartModel(this.getView().getConfiguration(), genusConfig.getSettings().getTitle(),
                genusConfig.getSettings().getSubtitle(), genusConfig.getSettings().getSubtitle(), tooltip, null, new PlotOptionsPie()));

        logger.info("Settings were added to a chart of " + this.getClass() + " with chart title: " + this.getView().getConfiguration().getTitle().getText());
    }

    @Override
    protected void addChartData() {

        //Add data for Genus
        DataSeries innerSeries = new DataSeries("Samples");
        innerSeries.setPlotOptions(innerPieOptions);

        Object[] genusData = this.genusConfig.getData().keySet().toArray(new Object[0]);

        List<DataSorter> dataSorters = new ArrayList<>();
        for (Object dataCategory : genusData) {
            for (int i = 0; i < this.genusConfig.getData().get(dataCategory).size(); i++) {
                dataSorters.add(new DataSorter(LabelFormatter.firstUpperRestLowerCase((String) this.genusConfig.getSettings().getxCategories().get(i)),
                        (int) this.genusConfig.getData().get(dataCategory).get(i)));
            }
        }

        Collections.sort(dataSorters);

        //Necessary to get colors right
        Number[] innerValues = new Number[dataSorters.size()];
        String[] innerNames = new String[dataSorters.size()];
        for (int i = 0; i < dataSorters.size(); i++) {
            innerNames[i] = dataSorters.get(i).getName();
            innerValues[i] = dataSorters.get(i).getCount();
        }
        innerSeries.setData(innerNames, innerValues, Arrays.copyOf(Colors.getSolidColors(), dataSorters.size()));

        //Add data for Species
        DataSeries outerSeries = new DataSeries("Samples");
        outerSeries.setPlotOptions(outerPieOptions);

        Object[] speciesData = speciesConfig.getData().keySet().toArray(new Object[0]);

        DataSeriesItem[] outerItems = new DataSeriesItem[0];
        //Actually adding of data
        for (Object dataCategory : speciesData) {
            outerItems = new DataSeriesItem[speciesConfig.getData().get(dataCategory).size()];
            int counter = 0;
            for (int i = 0; i < innerNames.length; i++) {
                List<DataSorter> dataSorterList = new ArrayList<>();

                //Add outer Series sorted
                for (String s : genusSpeciesMap.get(innerNames[i])) {
                    dataSorterList.add(new DataSorter(LabelFormatter.firstUpperRestLowerCase(s)
                            .concat(" [")
                            .concat(getSpeciesPercentage(s))
                            .concat("%]"), getSpeciesCount(s, (String) dataCategory)));
                }
                Collections.sort(dataSorterList);
                for (DataSorter d : dataSorterList) {
                    outerItems[counter] = new DataSeriesItem(d.getName(), d.getCount(), Colors.getRandomOpaque(Colors.getSolidColors()[i % Colors.getSolidColors().length]));
                    counter++;
                }
            }
        }

        outerSeries.setData(Arrays.asList(outerItems));

        this.getModel().addDonutPieData(innerSeries, outerSeries);

        logger.info("Data was added to a chart of " + this.getClass() + " with chart title: " + this.getView().getConfiguration().getTitle().getText());

    }

    private int getSpeciesCount(String species, String dataKey) {
        int value = 0;
        for (int j = 0; j < speciesConfig.getSettings().getxCategories().size(); j++) {
            if (speciesConfig.getSettings().getxCategories().get(j).equals(species)) {
                value = (int) speciesConfig.getData().get(dataKey).get(j);
                break;
            }
        }
        return value;
    }

    private String getSpeciesPercentage(String species) {
        String percentage = "";
        for (int j = 0; j < speciesConfig.getSettings().getxCategories().size(); j++) {
            if (speciesConfig.getSettings().getxCategories().get(j).equals(species)) {
                percentage = String.valueOf(speciesConfig.getSettings().getyCategories().get(j));
                break;
            }
        }
        return percentage;
    }

    @Override
    public void addChart(TabView tabView, String title) {

        tabView.addSubComponent(this.getModel(), this.getView());
        this.addReturnButtonListener(tabView);

        logger.info("View was added in " + this.getClass() + " for " + this.getView().getConfiguration().getTitle().getText());
    }

    @Override
    protected void addChartListener(){}

}