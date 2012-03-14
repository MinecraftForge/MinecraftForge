package argo.saj;

public interface JsonListener
{
    void startDocument();

    void endDocument();

    void startArray();

    void endArray();

    void startObject();

    void endObject();

    void startField(String var1);

    void endField();

    void stringValue(String var1);

    void numberValue(String var1);

    void trueValue();

    void falseValue();

    void nullValue();
}
