// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.bitwig.framework.daw.SendBankImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.resource.ChannelType;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.SettableColorValue;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChannelImpl extends AbstractDeviceChainImpl<Channel> implements IChannel
{
    protected final IValueChanger valueChanger;

    private static final int      MAX_RESOLUTION = 16384;

    private int                   vuLeft;
    private int                   vuRight;
    private IParameter            volumeParameter;
    private IParameter            panParameter;
    private ISendBank             sendBank;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The valueChanger
     * @param channel The channel
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public ChannelImpl (final IHost host, final IValueChanger valueChanger, final Channel channel, final int index, final int numSends)
    {
        super (index, channel);

        this.deviceChain = channel;
        this.valueChanger = valueChanger;

        if (channel == null)
            return;

        channel.exists ().markInterested ();
        channel.name ().markInterested ();
        channel.isActivated ().markInterested ();
        channel.mute ().markInterested ();
        channel.solo ().markInterested ();
        channel.color ().markInterested ();

        this.volumeParameter = new ParameterImpl (valueChanger, channel.volume (), 0);
        this.panParameter = new ParameterImpl (valueChanger, channel.pan (), 0);

        channel.addVuMeterObserver (MAX_RESOLUTION, 0, true, this::handleVULeftMeter);
        channel.addVuMeterObserver (MAX_RESOLUTION, 1, true, this::handleVURightMeter);

        this.sendBank = new SendBankImpl (host, valueChanger, numSends == 0 ? null : channel.sendBank (), numSends);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.deviceChain.exists ().setIsSubscribed (enable);
        this.deviceChain.name ().setIsSubscribed (enable);
        this.deviceChain.isActivated ().setIsSubscribed (enable);
        this.deviceChain.mute ().setIsSubscribed (enable);
        this.deviceChain.solo ().setIsSubscribed (enable);
        this.deviceChain.color ().setIsSubscribed (enable);

        this.volumeParameter.enableObservers (enable);
        this.panParameter.enableObservers (enable);

        this.sendBank.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.deviceChain.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        return ChannelType.UNKNOWN;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActivated ()
    {
        return this.deviceChain.isActivated ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean value)
    {
        this.deviceChain.isActivated ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        this.deviceChain.isActivated ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr ()
    {
        return this.volumeParameter.getDisplayedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr (final int limit)
    {
        return this.volumeParameter.getDisplayedValue (limit);
    }


    /** {@inheritDoc} */
    @Override
    public int getVolume ()
    {
        return this.volumeParameter.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeVolume (final int control)
    {
        this.volumeParameter.changeValue (control);
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final int value)
    {
        this.volumeParameter.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume ()
    {
        this.volumeParameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final boolean isBeingTouched)
    {
        this.volumeParameter.touchValue (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setVolumeIndication (final boolean indicate)
    {
        this.volumeParameter.setIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedVolume ()
    {
        return this.volumeParameter.getModulatedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr ()
    {
        return this.panParameter.getDisplayedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr (final int limit)
    {
        return this.panParameter.getDisplayedValue (limit);
    }


    /** {@inheritDoc} */
    @Override
    public int getPan ()
    {
        return this.panParameter.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void changePan (final int control)
    {
        this.panParameter.changeValue (control);
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final int value)
    {
        this.panParameter.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan ()
    {
        this.panParameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final boolean isBeingTouched)
    {
        this.panParameter.touchValue (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setPanIndication (final boolean indicate)
    {
        this.panParameter.setIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedPan ()
    {
        return this.panParameter.getModulatedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMute ()
    {
        return this.deviceChain.mute ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (final boolean value)
    {
        this.deviceChain.mute ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMute ()
    {
        this.deviceChain.mute ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSolo ()
    {
        return this.deviceChain.solo ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (final boolean value)
    {
        this.deviceChain.solo ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSolo ()
    {
        this.deviceChain.solo ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        final SettableColorValue color = this.deviceChain.color ();
        return new double []
        {
            color.red (),
            color.green (),
            color.blue ()
        };
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        this.deviceChain.color ().set ((float) red, (float) green, (float) blue);
    }


    /** {@inheritDoc} */
    @Override
    public int getVu ()
    {
        return (this.vuLeft + this.vuRight) * this.valueChanger.getUpperBound () / MAX_RESOLUTION / 2;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuLeft ()
    {
        return this.vuLeft * this.valueChanger.getUpperBound () / MAX_RESOLUTION;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuRight ()
    {
        return this.vuRight * this.valueChanger.getUpperBound () / MAX_RESOLUTION;
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        // Only available for track
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.deviceChain.duplicate ();
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.deviceChain.selectInEditor ();
        this.deviceChain.selectInMixer ();
        this.deviceChain.makeVisibleInArranger ();
        this.deviceChain.makeVisibleInMixer ();
    }


    /** {@inheritDoc} */
    @Override
    public ISendBank getSendBank ()
    {
        return this.sendBank;
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        // Intentionally empty
    }


    private void handleVULeftMeter (final int value)
    {
        // Limit value to this.configuration.getMaxParameterValue () due to
        // https://github.com/teotigraphix/Framework4Bitwig/issues/98
        this.vuLeft = value >= MAX_RESOLUTION ? MAX_RESOLUTION - 1 : value;
    }


    private void handleVURightMeter (final int value)
    {
        // Limit value to this.configuration.getMaxParameterValue () due to
        // https://github.com/teotigraphix/Framework4Bitwig/issues/98
        this.vuRight = value >= MAX_RESOLUTION ? MAX_RESOLUTION - 1 : value;
    }
}
