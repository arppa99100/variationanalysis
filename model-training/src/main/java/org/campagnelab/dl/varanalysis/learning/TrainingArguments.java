package org.campagnelab.dl.varanalysis.learning;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.campagnelab.dl.varanalysis.learning.architecture.SixDenseLayersForGPU;
import org.campagnelab.dl.varanalysis.learning.architecture.SixDenseLayersNarrower2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fac2003 on 8/20/16.
 */
@Parameters(commandDescription = "Train a model given training files and a validation file.")

public class TrainingArguments {
    @Parameter(names = {"-t", "--training-sets"}, variableArity=true, description = "Training sets, must be provided in .sbi/.sbip format (produced with Goby3). When more than one dataset is provided (multiple -t options), the " +
            "datasets are concatenated.")
    public List<String> trainingSets = new ArrayList<>();

    @Parameter(names = {"-v", "--validation-set"}, description = "Validation set, must be provided in .parquet/.info format.")
    public String validationSet = null;

    @Parameter(names = "--trio", description = "Use to train trio models. The training and validation datasets must have three samples, parents first, patient last.")
    public boolean isTrio = false;

    @Parameter(names = {"-n", "--num-training"}, description = "The maximum number of training samples to train with. ")
    public int numTraining = Integer.MAX_VALUE;

    @Parameter(names = {"-x", "--num-validation"}, description = "The maximum number of validation samples to read when evaluating performance. ")
    public int numValidation = Integer.MAX_VALUE;

    @Parameter(names = {"-s", "--random-seed"}, description = "The random seed to initialize network weights. ")
    public long seed = new Date().getTime();

    @Parameter(names = "--early-stopping-num-epochs", description = "The number of epochs without performance improvement before early stopping is triggered. ")
    public int stopWhenEpochsWithoutImprovement = 10;

    @Parameter(names = {"-r", "--learning-rate"}, description = "Learning rate.")
    public double learningRate = 0.1;

    @Parameter(names = "--regularization-rate", description = "Regularization rate. Disabled if set to NaN.")
    public double regularizationRate = Double.NaN;

    @Parameter(names = "--auc-clip-max-observations", description = "The maximum number of observations to sample when evaluating the AUC. ")
    public int aucClipMaxObservations = 10000;

    @Parameter(names = "--experimental-condition", description = "The experimental condition label used in validation loggin each epoch. ")
    public String experimentalCondition = "not_specified";

    @Parameter(names = "--mini-batch-size", description = "The size of the training minibatch")
    public int miniBatchSize = 32;

    @Parameter(names = {"--max-epochs"}, description = "The maximum number of epochs to train if early stopping does not occur")
    public int maxEpochs = Integer.MAX_VALUE;

    @Parameter(names = "--previous-model-path", description = "A model path to load parameters to continue training.")
    public String previousModelPath;

    @Parameter(names = "--previous-model-name", description = "The name of the previous model to load (i.e., \"bestAUC\", \"best\" or \"latest\" and continue training.")
    public String previousModelName = "bestAUC";

    @Parameter(names = "--net-architecture", description = "fully qualified classname that implements the choice of network architecture.")
    public java.lang.String architectureClassname= SixDenseLayersNarrower2.class.getCanonicalName();


    public String[] getTrainingSets() {
        return this.trainingSets.toArray(new String[this.trainingSets.size()]);
    }

}
