package org.campagnelab.dl.varanalysis.learning.features;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Intermediate representation of neural net features.
 * Created by fac2003 on 5/25/16.
 *
 * @author Fabien Campagne
 */
public class Features {
    private FloatArrayList values;

    public Features(FloatArrayList values) {
        this.values = values;
    }

    public Features(Features another) {
        this.values = new FloatArrayList(another.values);
    }

    public Features(int numFeatures) {
        this.values = new FloatArrayList(numFeatures);
        this.values.size(numFeatures);
    }

    public Features(INDArray inputs, int dimension) {
        final int size = inputs.size(dimension);
        values=new FloatArrayList(size);
        values.size(size);
        int[] indices = {dimension, 0};
        for (int i = 0; i < size; i++) {
            indices[1] = i;
            setFeatureValue(inputs.getFloat(indices), i);
        }
    }

    public float getFeatureValue(int index) {
        return values.getFloat(index);
    }

    public void setFeatureValue(float featureValue, int featureIndex) {
        this.values.set(featureIndex, featureValue);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Features) {
            Features other = (Features) obj;
            return other.values.equals(this.values);
        } return false;
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
