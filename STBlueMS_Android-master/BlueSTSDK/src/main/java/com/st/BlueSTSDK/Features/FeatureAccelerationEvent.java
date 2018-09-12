/*******************************************************************************
 * COPYRIGHT(c) 2015 STMicroelectronics
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 *   3. Neither the name of STMicroelectronics nor the names of its contributors
 *      may be used to endorse or promote products derived from this software
 *      without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 ******************************************************************************/

package com.st.BlueSTSDK.Features;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.st.BlueSTSDK.Feature;
import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.Utils.NumberConversion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Feature that will contain the different event that can be discovered using an accelerometer data.
 * The events can be generated by different algorithms that must be enabled using the function
 * {@link FeatureAccelerationEvent#detectEvent(DetectableEvent, boolean)}.
 * When an algorithm is enable the user can receive a notification thought the
 * {@link FeatureAccelerationEvent.FeatureAccelerationEventListener#onDetectableEventChange(com.st.BlueSTSDK.Features.FeatureAccelerationEvent, com.st.BlueSTSDK.Features.FeatureAccelerationEvent.DetectableEvent, boolean)}
 *  function.
 * <p>
 *     When the pedometer is enabled the you can use {@link
 *     FeatureAccelerationEvent#getPedometerSteps(com.st.BlueSTSDK.Feature.Sample)}
 *     for retrieve the number of steps. Otherwise the function will return a negative number
 * </p>
 * @author STMicroelectronics - Central Labs.
 * @version 2.0
 */
public class FeatureAccelerationEvent extends Feature {

    public static final String FEATURE_NAME = "Accelerometer Events";
    public static final String FEATURE_UNIT = null;
    public static final String FEATURE_DATA_NAME[] ={"Event","nSteps"} ;
    public static final Number DATA_MAX[] = {Short.MAX_VALUE, Short.MAX_VALUE};
    public static final Number DATA_MIN[] = {0,0};

    private static final int ACC_EVENT = 0;
    private static final int PEDOMETER_DATA = 1;
    /**
     * fake value used for notify a pedometer event
     */
    private static final short PEDOMETER_EVENT_ENUM_VALUE =0x100;

    /**
     * command used for disable the a specific event notification
     */
    private static final byte COMMAND_DISABLE[] = {0};

    /**
     * command used for enable the a specific event notification
     */
    private static final byte COMMAND_ENABLE[] = {1};


    ///////////////////////////////////////ACCELERATION EVENT - ENUM/UTIL//////////////////////////


    //define the event constant
    public static final int NO_EVENT = 0x00;
    public static final int ORIENTATION_TOP_RIGHT =0x01;
    public static final int ORIENTATION_BOTTOM_RIGHT=1<<1;
    public static final int ORIENTATION_BOTTOM_LEFT=0x03;
    public static final int ORIENTATION_TOP_LEFT=1<<2;
    public static final int ORIENTATION_UP=0x05;
    public static final int ORIENTATION_DOWN=0x06;
    public static final int TILT=1<<3;
    public static final int FREE_FALL=1<<4;
    public static final int SINGLE_TAP=1<<5;
    public static final int DOUBLE_TAP=1<<6;
    public static final int WAKE_UP=1<<7;
    public static final int PEDOMETER=1<<8;

    private static  final int ALL_ORIENTATION = ORIENTATION_TOP_RIGHT | ORIENTATION_BOTTOM_RIGHT | ORIENTATION_BOTTOM_LEFT
            | ORIENTATION_TOP_LEFT | ORIENTATION_UP | ORIENTATION_DOWN;

    //define the event name
    private static final String NO_EVENT_STR ="NO_EVENT";
    private static final String ORIENTATION_TOP_RIGHT_STR ="ORIENTATION_TOP_RIGHT";
    private static final String ORIENTATION_BOTTOM_RIGHT_STR ="ORIENTATION_BOTTOM_RIGHT";
    private static final String ORIENTATION_BOTTOM_LEFT_STR ="ORIENTATION_BOTTOM_LEFT";
    private static final String ORIENTATION_TOP_LEFT_STR ="ORIENTATION_TOP_LEFT";
    private static final String ORIENTATION_UP_STR ="ORIENTATION_UP";
    private static final String ORIENTATION_DOWN_STR ="ORIENTATION_DOWN";
    private static final String TILT_STR ="TILT";
    private static final String FREE_FALL_STR ="FREE_FALL";
    private static final String SINGLE_TAP_STR ="SINGLE_TAP";
    private static final String DOUBLE_TAP_STR ="DOUBLE_TAP";
    private static final String WAKE_UP_STR ="WAKE_UP";
    private static final String PEDOMETER_STR ="PEDOMETER";

    //define the annotation
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true,
            value = {NO_EVENT, ORIENTATION_TOP_LEFT,ORIENTATION_TOP_RIGHT,ORIENTATION_BOTTOM_RIGHT,
            ORIENTATION_BOTTOM_LEFT,ORIENTATION_UP,ORIENTATION_DOWN,TILT,SINGLE_TAP,DOUBLE_TAP,
                    WAKE_UP,PEDOMETER,FREE_FALL})
    public @interface AccelerationEvent {}

    /**
     * tell if the event has one of the orientation value
     * @param event event to test
     * @return true if the event has one of the orientation event
     */
    public static boolean hasOrientationEvent(@AccelerationEvent int event){
        return extractOrientationEvent(event)!=NO_EVENT;
    }

    /**
     * remove all the event except the orientation ones
     * @param event event to filter
     * @return orientation event
     */
    public static @AccelerationEvent int extractOrientationEvent(@AccelerationEvent int event){
        return  event & ALL_ORIENTATION;
    }

    /**
     * convert an event to a string
     * @param event event
     * @return string that describe the event
     */
    public static String eventToString(@AccelerationEvent int event){
        if(event==NO_EVENT)
            return NO_EVENT_STR;

        StringBuilder eventStr = new StringBuilder();
        if((event & 0x07) !=0){ //has an orientation event
            if(event==ORIENTATION_BOTTOM_LEFT)
                eventStr.append(ORIENTATION_BOTTOM_LEFT_STR);
            else if(event== ORIENTATION_BOTTOM_RIGHT)
                eventStr.append(ORIENTATION_BOTTOM_RIGHT_STR);
            else if(event== ORIENTATION_DOWN)
                eventStr.append(ORIENTATION_DOWN_STR);
            else if(event== ORIENTATION_TOP_LEFT)
                eventStr.append(ORIENTATION_TOP_LEFT_STR);
            else if(event== ORIENTATION_TOP_RIGHT)
                eventStr.append(ORIENTATION_TOP_RIGHT_STR);
            else if(event== ORIENTATION_UP)
                eventStr.append(ORIENTATION_UP_STR);
            eventStr.append(' ');
        }

        if((event & DOUBLE_TAP) !=0)
            eventStr.append(DOUBLE_TAP_STR+' ');

        if((event & PEDOMETER) !=0)
            eventStr.append(PEDOMETER_STR+' ');

        if((event & SINGLE_TAP) !=0)
            eventStr.append(SINGLE_TAP_STR+' ');

        if((event & TILT) !=0)
            eventStr.append(TILT_STR+' ');

        if((event & FREE_FALL) !=0)
            eventStr.append(FREE_FALL_STR+' ');

        if((event & WAKE_UP) !=0)
            eventStr.append(WAKE_UP_STR+' ');

        if(eventStr.charAt(eventStr.length()-1)==' ') // last char is a space
            eventStr.setLength(eventStr.length()-1);

        return eventStr.toString();
    }

    ///////////////////////////////////ACCELERATION EVENT - ENUM/UTIL END//////////////////////////

    /**
     * Algorithm that can run in the accelerometer
     */
    public enum DetectableEvent {
        NONE((byte)'\0'),
        MULTIPLE((byte)'m'),
        ORIENTATION((byte)'o'),
        PEDOMETER((byte)'p'),
        SINGLE_TAP((byte)'s'),
        DOUBLE_TAP((byte)'d'),
        FREE_FALL((byte)'f'),
        WAKE_UP((byte)'w'),
        TILT((byte)'t');

        /**
         * byte used as command type when we enable/disable the algorithm
         */
        private byte typeId;

        DetectableEvent(byte b){
            typeId=b;
        }

        byte getTypeId() {
            return typeId;
        }

        /**
         * create DetectableEvent starting from the byte value
         * @param b command type byte
         * @return null if is not a value command type or the algorithm that generate the command
         */
        @Nullable
        static DetectableEvent build(byte b){
            for (DetectableEvent event: DetectableEvent.values() ) {
                if(event.getTypeId()==b)
                    return  event;
            }//for
            return  null;
        }//build

        @Override
        public String toString() {
            switch (this){
                case NONE:
                    return "None";
                case MULTIPLE:
                    return "Multiple";
                case ORIENTATION:
                    return "Orientation";
                case PEDOMETER:
                    return "Pedometer";
                case SINGLE_TAP:
                    return "Single Tap";
                case DOUBLE_TAP:
                    return "Double Tap";
                case FREE_FALL:
                    return "Free Fall";
                case WAKE_UP:
                    return "Wake Up";
                case TILT:
                    return "Tilt";
                default:
                    return super.toString();
            }
        }
    }//DetectableEvent

    /**
     * event that is currently detected by the accelerometer or null if nothing is enabled
     */
    private DetectableEvent mEnabledEvent = DetectableEvent.NONE;

    /**
     * extract the Event from a sensor sample
     * @param sample data read from the node
     * @return type of event detected by the node
     */
    @SuppressWarnings("ResourceType") // we are secure that the int is an or of acceleration event
    public static @AccelerationEvent int getAccelerationEvent(Sample sample){
        if(hasValidIndex(sample,ACC_EVENT))
            return sample.data[ACC_EVENT].intValue();
        return NO_EVENT;
    }//getAccelerationEvent

    /**
     * test if the sample contains a specific event
     * @param sample node sample
     * @param event event to search
     * @return true if the sample has the searched event
     */
    public static boolean hasAccelerationEvent(Sample sample,@AccelerationEvent int event){
        return (getAccelerationEvent(sample) & event)!=0;
    }

    /**
     * if you are running the pedometer, calling this function you can read the number of steps done
     * @param sample sample to read
     * @return number of steps or a negative number if the pedometer isn't enabled
     */
    public static int getPedometerSteps(Sample sample){
        if(hasValidIndex(sample,PEDOMETER_DATA))
            return sample.data[PEDOMETER_DATA].intValue();
        return -1;
    }//getPedometerSteps

    /**
     * build a activity feature
     * @param n node that will send data to this feature
     */
    public FeatureAccelerationEvent(Node n) {
        super(FEATURE_NAME, n, new Field[]{
                new Field(FEATURE_DATA_NAME[ACC_EVENT], FEATURE_UNIT, Field.Type.UInt16,
                        DATA_MAX[ACC_EVENT],DATA_MIN[ACC_EVENT]),
                new Field(FEATURE_DATA_NAME[PEDOMETER_DATA], FEATURE_UNIT, Field.Type.UInt16,
                        DATA_MAX[PEDOMETER_DATA],DATA_MIN[PEDOMETER_DATA])
        });
    }//FeatureActivity

    /**
     * read a byte with the event data send from the node
     * @param timestamp data timestamp
     * @param data       array where read the data
     * @param dataOffset offset where start to read the data
     * @return number of read byte (1) and data extracted (the Activity id)
     * @throws IllegalArgumentException if the data array has not enough data
     *
     * 3 bytes: acc event + #steps
     * 2 bytes: #steps or acc Event
     *
     */
    @Override
    protected ExtractResult extractData(long timestamp, @NonNull byte[] data, int dataOffset) {

        short accEvent;
        int nSteps=-1;
        int readBytes;

        if (data.length - dataOffset >= 3){
            accEvent = (short)
                    (NumberConversion.byteToUInt8(data, dataOffset + 0) | PEDOMETER);
            nSteps = NumberConversion.LittleEndian.bytesToUInt16(data, dataOffset + 1);
            readBytes=3;
        }else if (data.length - dataOffset == 2){
            if(mEnabledEvent==DetectableEvent.PEDOMETER) {
                accEvent = PEDOMETER;
                nSteps = NumberConversion.LittleEndian.bytesToUInt16(data, dataOffset);
            }else{
                accEvent =  NumberConversion.byteToUInt8(data, dataOffset + 0);
            }//if-else
            readBytes=2;
        }else
            throw new IllegalArgumentException("There are no 2 byte available to read");

        return new ExtractResult(
                new Sample(timestamp,new Number[]{accEvent,nSteps},getFieldsDesc()),
                readBytes);
    }//extractData

    /**
     * start looking for an event type
     * @param event algorithm to enable/disable
     * @param enable true for enable it, false for disable it
     * @return true if the command is correctly send or the algorithm is already enabled
     * <p>
     *     Only one algorithm can run at time, enabling an algorithm will disable the previous one.
     * </p>
     */
    public boolean detectEvent(DetectableEvent event, boolean enable){

        if(enable && event != mEnabledEvent && mEnabledEvent!=DetectableEvent.NONE)
            sendCommand(mEnabledEvent.getTypeId(),COMMAND_DISABLE); //disable the previous one

        if(event != DetectableEvent.NONE)
            return sendCommand(event.getTypeId(), enable ? COMMAND_ENABLE : COMMAND_DISABLE);
        else{
            notifyEventEnabled(DetectableEvent.NONE,true);
            return true;
        }
    }

    /**
     * @return the algorithm that is running or null if no algorithm are running
     */
    public @Nullable
    DetectableEvent getEnabledEvent(){
        return mEnabledEvent;
    }

    /**
     * notify to all the listener of type FeatureAccelerationEventListener that the status
     * of an algorithm is changed
     * @param event event that change
     * @param status new status
     */
    protected void notifyEventEnabled(final DetectableEvent event,final boolean status) {
        for (final FeatureListener listener : mFeatureListener) {
            if (listener instanceof FeatureAccelerationEventListener)
                sThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        ((FeatureAccelerationEventListener) listener)
                                .onDetectableEventChange(FeatureAccelerationEvent.this, event, status);
                    }//run
                });
        }//for
    }//notifyUpdate

    /**
     *
     * @param timeStamp device time stamp of when the response was send
     * @param commandType id of the request that the feature did
     * @param data data attached to the response
     */
    @Override
    protected void parseCommandResponse(int timeStamp, byte commandType, byte[] data) {

        DetectableEvent event= DetectableEvent.build(commandType);
        if(event==null)
            return;

        boolean status = data[0]==1;

        if(status){
            mEnabledEvent=event;
        }else if(mEnabledEvent==event){
            mEnabledEvent=DetectableEvent.NONE;
        }//if-else-if

        notifyEventEnabled(event,status);
    }

    @Override
    public String toString(){
        Sample sample = mLastSample;
        if(sample!=null){
            @AccelerationEvent int event = getAccelerationEvent(sample);
            String pedometerData="";
            if((event & PEDOMETER)!=0)
                pedometerData = "\n\tnSteps: "+getPedometerSteps(sample);
            return FEATURE_NAME+":\n"+
                    "\tTimestamp: "+ sample.timestamp+"\n" +
                    "\tEvent: "+ eventToString(event) +
                    pedometerData;

        }else{
            return super.toString();
        }//if-else

    }//toString

    /**
     * Extend the FeatureListener for notify also the enabling/disabling of a new algorithm
     */
    public interface FeatureAccelerationEventListener extends Feature.FeatureListener {

        /**
         * An event detection algorithm is enable or disabled
         *
         * @param f feature where the event change
         * @param event event that has change its status
         * @param newStatus true if the event is enabled, false otherwise
         */
        void onDetectableEventChange(FeatureAccelerationEvent f, DetectableEvent event, boolean newStatus);
    }
}
