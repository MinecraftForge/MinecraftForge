package argo.jdom;

interface Functor
{
    boolean matchesNode(Object var1);

    Object applyTo(Object var1);

    String shortForm();
}
