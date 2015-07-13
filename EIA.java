package com.agileFTP;

import java.util.HashMap;

/**
 * Created by Paul on 7/12/2015.
 */
public interface EIA {

    String getDecorator();

    boolean execute(String []input);

    boolean init(HashMap main);
}
