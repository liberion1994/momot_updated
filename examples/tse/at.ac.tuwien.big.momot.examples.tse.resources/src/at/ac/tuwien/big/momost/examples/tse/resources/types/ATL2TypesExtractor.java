package at.ac.tuwien.big.momost.examples.tse.resources.types;

import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IExtractor;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.emf.EMFExtractor;

import at.ac.tuwien.big.momot.examples.tse.resources.ATL2Model;

public class ATL2TypesExtractor {

	public static void extract(String atlFile, String atlModel, String inputMM, String outputMM, String typesModel) 
			throws ATLCoreException, IOException {
		
		IModel model = ATL2Model.toModel(atlFile);
		
		IExtractor extractor = new EMFExtractor();
		extractor.extract(model, atlModel);
		
		ExtractRuleTypes runner = new ExtractRuleTypes();
		runner.loadModels(
				atlModel, 
				inputMM, 
				outputMM);
		
		runner.doExtractRuleTypes(new NullProgressMonitor());
		runner.saveModels(typesModel);
	}
	
	public static void extract(String atlFile, String inputMM, String outputMM, String typesModel) 
			throws ATLCoreException, IOException {
		extract(atlFile, atlFile.replaceFirst(".atl", "_ATL.xmi"), inputMM, outputMM, typesModel);
	}
	
	public static void extract(String atlFile, String inputMM, String outputMM) 
			throws ATLCoreException, IOException {
		extract(atlFile, atlFile.replaceFirst(".atl", "_ATL.xmi"), inputMM, outputMM, atlFile.replaceFirst(".atl", "_Types.xmi"));
	}
	
	public static void main(String[] args) {
		try {
			extract("casestudy/OCL2R2ML/transformations/OCL2R2ML/OCL2R2ML.atl", 
					"casestudy/OCL2R2ML/metamodels/OCL/OCL.ecore", 
					"casestudy/OCL2R2ML/metamodels/r2ml/r2ml.ecore");
		} catch (ATLCoreException | IOException e) {
			e.printStackTrace();
		}
	}
}
