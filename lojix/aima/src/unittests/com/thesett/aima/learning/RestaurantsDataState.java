/*
 * Copyright The Sett Ltd, 2005 to 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.aima.learning;

import java.util.ArrayList;
import java.util.Collection;

import com.thesett.aima.attribute.impl.BooleanAttribute;
import com.thesett.aima.attribute.impl.EnumAttribute;
import com.thesett.aima.attribute.impl.EnumeratedStringAttribute;
import com.thesett.aima.state.State;
import com.thesett.aima.state.impl.MapBackedState;

/**
 * RestaurantDataState is used to represent data for the restaurant will-wait classification learning example from the
 * AIMA text book. An agent has to decide whether it will wait at a restaurant to be served given inputs describing the
 * situation. The 'real' decision function is as chosen by one of the authors given his 'expert' opinion on how he
 * decides.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class RestaurantsDataState extends MapBackedState implements State
{
    /**  */
    /* private static final Logger log = Logger.getLogger(RestaurantsDataState.class.getName()); */

    /**  */
    static EnumAttribute.EnumAttributeFactory patronsAttributeFactory = EnumAttribute.getFactoryForClass(Patrons.class);

    /**  */
    static EnumAttribute.EnumAttributeFactory priceAttributeFactory = EnumAttribute.getFactoryForClass(Price.class);

    /**  */
    static EnumAttribute.EnumAttributeFactory typeAttributeFactory = EnumAttribute.getFactoryForClass(Type.class);

    /**  */
    static EnumAttribute.EnumAttributeFactory waitAttributeFactory = EnumAttribute.getFactoryForClass(Wait.class);

    /**  */
    static EnumeratedStringAttribute.EnumeratedStringAttributeFactory booleanFactory =
        EnumeratedStringAttribute.getFactoryForClass("boolean");

    static
    {
        booleanFactory.createStringAttribute("true");
        booleanFactory.createStringAttribute("false");
        booleanFactory.finalizeAttribute();
    }

    /** The training data set. */
    public static Collection<RestaurantsDataState> trainingData;

    static
    {
        trainingData = new ArrayList<RestaurantsDataState>();
        trainingData.add(new RestaurantsDataState(true, false, false, true, Patrons.Some, Price.Expensive, false, true,
                Type.French, Wait.TenMinutes, true));

        trainingData.add(new RestaurantsDataState(true, false, false, true, Patrons.Full, Price.Cheap, false, false,
                Type.Thai, Wait.HalfAnHour, false));

        trainingData.add(new RestaurantsDataState(false, true, false, false, Patrons.Some, Price.Cheap, false, false,
                Type.Burger, Wait.HalfAnHour, true));

        trainingData.add(new RestaurantsDataState(true, false, true, true, Patrons.Full, Price.Cheap, false, false,
                Type.Thai, Wait.TenMinutes, true));

        trainingData.add(new RestaurantsDataState(true, false, true, false, Patrons.Full, Price.Expensive, false, true,
                Type.French, Wait.HourOrMore, false));

        trainingData.add(new RestaurantsDataState(false, true, false, true, Patrons.Some, Price.MidPrice, true, true,
                Type.Italian, Wait.TenMinutes, true));

        trainingData.add(new RestaurantsDataState(false, true, false, false, Patrons.None, Price.Cheap, true, false,
                Type.Burger, Wait.TenMinutes, false));

        trainingData.add(new RestaurantsDataState(false, false, false, true, Patrons.Some, Price.MidPrice, true, true,
                Type.Thai, Wait.TenMinutes, true));

        trainingData.add(new RestaurantsDataState(false, true, true, false, Patrons.Full, Price.Cheap, true, false,
                Type.Burger, Wait.HourOrMore, false));

        trainingData.add(new RestaurantsDataState(true, true, true, true, Patrons.Full, Price.Expensive, false, true,
                Type.Italian, Wait.HalfAnHour, false));

        trainingData.add(new RestaurantsDataState(false, false, false, false, Patrons.None, Price.Cheap, false, false,
                Type.Thai, Wait.TenMinutes, false));

        trainingData.add(new RestaurantsDataState(true, true, true, true, Patrons.Full, Price.Cheap, false, false,
                Type.Burger, Wait.HalfAnHour, true));
    }

    /** The test data set. */
    public static Collection<State> testData;

    static
    {
        testData = new ArrayList<State>();

        testData.add(new RestaurantsDataState(true, false, false, true, Patrons.Some, Price.Expensive, false, true,
                Type.French, Wait.TenMinutes, true));

        // and so on...
    }

    /** Enum of the possible values of the price attribute. */
    public enum Price
    {
        Cheap, MidPrice, Expensive
    }

    /** Enum of the possible values of the type attribute. */
    public enum Type
    {
        French, Italian, Thai, Burger
    }

    /** Enum of the possible values of the number of patrons attribute. */
    public enum Patrons
    {
        None, Some, Full
    }

    /** Enum of the possible values of the length of wait attribute. */
    public enum Wait
    {
        TenMinutes, HalfAnHour, HourOrMore
    }

    /** Creates a new RestaurantsDataState object. */
    public RestaurantsDataState(boolean alternate, boolean bar, boolean friSat, boolean hungry, Patrons patrons,
        Price price, boolean raining, boolean reservation, Type type, Wait wait, boolean willWait)
    {
        // Set the input properties Could use a boolean attribute here but a string attribute is used just to exercise
        // the EnumeratedStringAttribute class.
        setProperty("alternate",
            alternate ? booleanFactory.createStringAttribute("true") : booleanFactory.createStringAttribute("false"));
        setProperty("bar", new BooleanAttribute(bar));
        setProperty("friSat", new BooleanAttribute(friSat));
        setProperty("hungry", new BooleanAttribute(hungry));
        setProperty("patrons", patronsAttributeFactory.createEnumAttribute(patrons));
        setProperty("price", priceAttributeFactory.createEnumAttribute(price));
        setProperty("raining", new BooleanAttribute(raining));
        setProperty("reservation", new BooleanAttribute(reservation));
        setProperty("type", typeAttributeFactory.createEnumAttribute(type));
        setProperty("wait", waitAttributeFactory.createEnumAttribute(wait));

        // Set the goal property
        // setProperty("willWait", new BooleanAttribute(willWait));
        setProperty("goal", new BooleanAttribute(willWait));
    }

    /** Returns whether this state is a goal node. Returns true if will-wait is true, false if it is false. */
    public boolean isGoal()
    {
        return ((BooleanAttribute) getProperty("goal")).booleanValue();
    }

    /** @return */
    public String toString()
    {
        return "alternate = " + getProperty("alternate") + ", " + "bar = " + getProperty("bar") + ", " + "friSat = " +
            getProperty("friSat") + ", " + "hungry = " + getProperty("hungry") + ", " + "patrons = " +
            getProperty("patrons") + ", " + "price = " + getProperty("price") + ", " + "raining = " +
            getProperty("raining") + ", " + "reservation = " + getProperty("reservation") + ", " + "type = " +
            getProperty("type") + ", " + "wait = " + getProperty("wait") + ", " + "goal = " + getProperty("goal");
    }
}
