package org.example.apitest.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BeanUtilsAdvanced {

    public static void copyProperties(Object source, Object target, String ... listIgnoreProperties) {
        String[] ignoreProperties  = getNullPropertyNames(source);
        List<String> arraylist = new ArrayList<>(Arrays.asList(ignoreProperties));
        arraylist.addAll(Arrays.asList(listIgnoreProperties));
        ignoreProperties = arraylist.toArray(ignoreProperties);
        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
