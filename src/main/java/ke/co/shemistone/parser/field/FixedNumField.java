/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.co.shemistone.parser.field;

import ke.co.shemistone.parser.Converters;
import ke.co.shemistone.parser.Strings;

/**
 *
 * @author shemistone
 */
public class FixedNumField implements Field {

    private int id;
    private int length;
    private Encoding valueEncoding;
    private Padder padder;
    private String value = "";
    private String encodedValue = "";

    public FixedNumField(IsoField isoField) {
        this.id = isoField.getId();
        this.length = isoField.getLength();
        this.valueEncoding = isoField.getValueEncoding();
        this.padder = isoField.getPadder();
    }

    @Override
    public String encode() {
        if (this.value.length() == 0) {
            return this.encodedValue;
        }
        if (this.value.length() > this.length) {
            throw new PackException(String.format("Length of field %d (%s) is more than %d", this.id, this.value.length(), this.length));
        }
        switch (this.padder) {
            case ZERO_PREPENDER:
            default:
                this.value = Strings.prepend(this.value, "0", this.length);
                break;
        }
        switch (this.valueEncoding) {
            case BCD://0800
                this.encodedValue = Converters.hexToAscii(this.value);
                break;
            case ASC:
            default:
                this.encodedValue = this.value;
                break;
        }
        return this.encodedValue;
    }

    @Override
    public int decode(String head) {
        int nextHeadIndex;
        switch (this.valueEncoding) {
            case BCD:
                if (this.length % 2 != 0) {
                    this.length = this.length + 1;
                }
                nextHeadIndex = this.length / 2;
                this.encodedValue = head.substring(0, nextHeadIndex);
                head = Converters.asciiToHex(head);
                break;
            case ASC:
            default:
                nextHeadIndex = this.length;
                this.encodedValue = head.substring(0, nextHeadIndex);
                break;
        }
        this.value = head.substring(0, this.length);
        return nextHeadIndex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Encoding getValueEncoding() {
        return valueEncoding;
    }

    public void setValueEncoding(Encoding valueEncoding) {
        this.valueEncoding = valueEncoding;
    }

    public Padder getPadder() {
        return padder;
    }

    public void setPadder(Padder padder) {
        this.padder = padder;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getEncodedValue() {
        return this.encodedValue;
    }

    @Override
    public void setEncodedValue(String encodedValue) {
        this.encodedValue = encodedValue;
    }

}
