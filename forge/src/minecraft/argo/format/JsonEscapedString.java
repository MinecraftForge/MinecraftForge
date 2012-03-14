package argo.format;

final class JsonEscapedString
{
    private final String escapedString;

    JsonEscapedString(String par1Str)
    {
        this.escapedString = par1Str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public String toString()
    {
        return this.escapedString;
    }
}
