package pl.edu.icm.cermine;

import java.io.InputStream;
import org.jdom.Element;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * NLM-based content extractor from PDF files.
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMContentExtractor implements DocumentContentExtractor<Element> {
  
    /** geometric structure extractor */
    private DocumentStructureExtractor structureExtractor;
    
    /** document metadata extractor from geometric structure */
    private DocumentMetadataExtractor<Element> metadataExtractor;
    
    /** parsed references extractor from geometric structure */
    private DocumentReferencesExtractor<Element> referencesExtractor;

    public PdfNLMContentExtractor() throws AnalysisException {
        structureExtractor = new PdfBxStructureExtractor();
        metadataExtractor = new PdfNLMMetadataExtractor();
        referencesExtractor = new PdfNLMReferencesExtractor();
    }

    public PdfNLMContentExtractor(DocumentStructureExtractor structureExtractor, DocumentMetadataExtractor<Element> metadataExtractor, DocumentReferencesExtractor<Element> referencesExtractor) {
        this.structureExtractor = structureExtractor;
        this.metadataExtractor = metadataExtractor;
        this.referencesExtractor = referencesExtractor;
    }
    
    public PdfNLMContentExtractor(InputStream initialModel, InputStream initialRange, 
            InputStream metadataModel, InputStream metadataRange, InputStream refModel) throws AnalysisException {
        structureExtractor = new PdfBxStructureExtractor(initialModel, initialRange);
        metadataExtractor = new PdfNLMMetadataExtractor(metadataModel, metadataRange);
        referencesExtractor = new PdfNLMReferencesExtractor(refModel);
    }
    
    /**
     * Extracts content from PDF file and stored it in NLM format.
     * 
     * @param stream
     * @return extracted content in NLM format
     * @throws AnalysisException 
     */
    @Override
    public Element extractContent(InputStream stream) throws AnalysisException {
        BxDocument document = structureExtractor.extractStructure(stream);
        return extractContent(document);
    }

    /**
     * Extracts content from PDF file and stored it in NLM format.
     * 
     * @param document
     * @return extracted content in NLM format
     * @throws AnalysisException 
     */
    @Override
    public Element extractContent(BxDocument document) throws AnalysisException {
        Element content = metadataExtractor.extractMetadata(document);
        Element[] references = referencesExtractor.extractReferences(document);
        
        Element back = content.getChild("back");
        Element refList = back.getChild("ref-list");
        for (Element ref : references) {
            Element r = new Element("ref");
            r.addContent(ref);
            refList.addContent(r);
        }
        
        return content;
    }
    
}