package pl.edu.icm.cermine.content.filtering;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.content.filtering.features.*;
import pl.edu.icm.cermine.evaluation.EvaluationUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.general.SimpleFeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ContentFilterTools {

    public static final FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new SimpleFeatureVectorBuilder<BxZone, BxPage>();
    static {
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
                new AreaFeature(),
                new FigureTableFeature(),
                new GreekLettersFeature(),
                new RelativeMeanLengthFeature(),
                new MathSymbolsFeature(),
                new XVarianceFeature()
                ));
    }
    
    public static List<TrainingSample<BxZoneLabel>> toTrainingSamples(String trainPath) throws AnalysisException {
        List<BxDocument> documents = EvaluationUtils.getDocumentsFromPath(trainPath);
        return toTrainingSamples(documents);
    }
    
    public static List<TrainingSample<BxZoneLabel>> toTrainingSamples(List<BxDocument> documents) throws AnalysisException {
        List<TrainingSample<BxZoneLabel>> TrainingSamples;

        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(1.0);
        
        Map<BxZoneLabel, BxZoneLabel> map = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);
        map.put(BxZoneLabel.BODY_HEADER, BxZoneLabel.BODY_CONTENT);
       
        TrainingSamples = BxDocsToTrainingSamplesConverter.getTrainingSamples(documents, vectorBuilder, map);
        TrainingSamples = ClassificationUtils.filterElements(TrainingSamples, BxZoneLabelCategory.CAT_BODY);
        TrainingSamples = selector.pickElements(TrainingSamples);
        
        return TrainingSamples;
    }
    
}
