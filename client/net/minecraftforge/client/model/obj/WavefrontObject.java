package net.minecraftforge.client.model.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WavefrontObject
{

    private static final String REGEX_VERTEX = "(v( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(v( (\\-){0,1}\\d+\\.\\d+){3,4} *$)";
    private static final String REGEX_VERTEX_NORMAL = "(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *$)";
    private static final String REGEX_TEXTURE_COORDINATE = "(vt( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vt( (\\-){0,1}\\d+\\.\\d+){3,4} *$)";
    private static final String REGEX_FACE_VERTEX_TEXTURECOORD_VERTEXNORMAL = "(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)";
    private static final String REGEX_FACE_VERTEX_TEXTURECOORD = "(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)";
    private static final String REGEX_FACE_VERTEX_VERTEXNORMAL = "(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)";
    private static final String REGEX_FACE_VERTEX = "(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)";
    private static final String REGEX_GROUP_OBJECT = "([go]( [\\w\\d]+) *\\n)|([go]( [\\w\\d]+) *$)";

    public ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    public ArrayList<Vertex> vertexNormals = new ArrayList<Vertex>();
    public ArrayList<TextureCoordinate> textureCoordinates = new ArrayList<TextureCoordinate>();
    public ArrayList<GroupObject> groupObjects = new ArrayList<GroupObject>();
    private GroupObject currentGroupObject;

    public WavefrontObject(String fileName)
    {
        try
        {
            parseObjModel(this.getClass().getResource(fileName));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    public WavefrontObject(URL fileURL)
    {
        try
        {
            parseObjModel(fileURL);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void parseObjModel(URL fileURL) throws ParseException
    {
        BufferedReader reader = null;
        InputStream inputStream = null;
        
        String currentLine = null;
        int lineCount = 0;

        try
        {
            inputStream = fileURL.openStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((currentLine = reader.readLine()) != null)
            {
                lineCount++;
                currentLine = currentLine.replaceAll("\\s+", " ").trim();

                if (currentLine.startsWith("#") || currentLine.length() == 0)
                {
                    continue;
                }
                else if (currentLine.startsWith("v "))
                {
                    Vertex vertex = parseVertex(currentLine);
                    if (vertex != null)
                    {
                        vertices.add(vertex);
                    }
                    else
                    {
                        throw new ParseException("Error parsing entry ('" + currentLine + "'" + ", line " + lineCount + ") in file '" + fileURL.getFile() + "'", lineCount);
                    }
                }
                else if (currentLine.startsWith("vn "))
                {
                    Vertex vertex = parseVertexNormal(currentLine);
                    if (vertex != null)
                    {
                        vertexNormals.add(vertex);
                    }
                    else
                    {
                        throw new ParseException("Error parsing entry ('" + currentLine + "'" + ", line " + lineCount + ") in file '" + fileURL.getFile() + "'", lineCount);
                    }
                }
                else if (currentLine.startsWith("vt "))
                {
                    TextureCoordinate textureCoordinate = parseTextureCoordinate(currentLine);
                    if (textureCoordinate != null)
                    {
                        textureCoordinates.add(textureCoordinate);
                    }
                    else
                    {
                        throw new ParseException("Error parsing entry ('" + currentLine + "'" + ", line " + lineCount + ") in file '" + fileURL.getFile() + "'", lineCount);
                    }
                }
                else if (currentLine.startsWith("f "))
                {

                    if (currentGroupObject == null)
                    {
                        currentGroupObject = new GroupObject("Default");
                    }

                    Face face = parseFace(currentLine);

                    if (face != null)
                    {
                        currentGroupObject.faces.add(face);
                    }
                    else
                    {
                        throw new ParseException("Error parsing entry ('" + currentLine + "'" + ", line " + lineCount + ") in file '" + fileURL.getFile() + "'", lineCount);
                    }
                }
                else if (currentLine.startsWith("g ") | currentLine.startsWith("o "))
                {
                    GroupObject group = parseGroupObject(currentLine);

                    if (group != null)
                    {
                        if (currentGroupObject != null)
                        {
                            groupObjects.add(currentGroupObject);
                        }
                    }
                    else
                    {
                        throw new ParseException("Error parsing entry ('" + currentLine + "'" + ", line " + lineCount + ") in file '" + fileURL.getFile() + "'", lineCount);
                    }

                    currentGroupObject = group;
                }
            }

            groupObjects.add(currentGroupObject);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void renderAll()
    {
        Tessellator tessellator = Tessellator.instance;

        if (currentGroupObject != null)
        {
            tessellator.startDrawing(currentGroupObject.glDrawingMode);
        }
        else
        {
            tessellator.startDrawing(GL11.GL_TRIANGLES);
        }

        for (GroupObject groupObject : groupObjects)
        {
            groupObject.render(tessellator);
        }

        tessellator.draw();
    }

    public void renderOnly(String... groupNames)
    {
        for (GroupObject groupObject : groupObjects)
        {
            for (String groupName : groupNames)
            {
                if (groupName.equalsIgnoreCase(groupObject.name))
                {
                    groupObject.render();
                }
            }
        }
    }

    public void renderAllExcept(String... excludedGroupNames)
    {
        for (GroupObject groupObject : groupObjects)
        {
            for (String excludedGroupName : excludedGroupNames)
            {
                if (!excludedGroupName.equalsIgnoreCase(groupObject.name))
                {
                    groupObject.render();
                }
            }
        }
    }

    private Vertex parseVertex(String line)
    {
        Vertex vertex = null;

        if (isValidVertexLine(line))
        {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try
            {
                if (tokens.length == 3)
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }

        return vertex;
    }

    private Vertex parseVertexNormal(String line)
    {
        Vertex vertexNormal = null;

        if (isValidVertexNormalLine(line))
        {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try
            {
                if (tokens.length == 3)
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }

        return vertexNormal;
    }

    private TextureCoordinate parseTextureCoordinate(String line)
    {
        TextureCoordinate textureCoordinate = null;

        if (isValidTextureCoordinateLine(line))
        {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try
            {
                if (tokens.length == 2)
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]));
                else if (tokens.length == 3)
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }

        return textureCoordinate;
    }

    private Face parseFace(String line)
    {
        Face face = null;

        if (isValidFaceLine(line))
        {
            face = new Face();

            String trimmedLine = line.substring(line.indexOf(" ") + 1);
            String[] tokens = trimmedLine.split(" ");
            String[] subTokens = null;

            if (tokens.length == 3)
            {
                if (currentGroupObject.glDrawingMode == -1)
                {
                    currentGroupObject.glDrawingMode = GL11.GL_TRIANGLES;
                }
                else if (currentGroupObject.glDrawingMode != GL11.GL_TRIANGLES)
                {
                    return null;
                }
            }
            else if (tokens.length == 4)
            {
                if (currentGroupObject.glDrawingMode == -1)
                {
                    currentGroupObject.glDrawingMode = GL11.GL_QUADS;
                }
                else if (currentGroupObject.glDrawingMode != GL11.GL_QUADS)
                {
                    return null;
                }
            }

            face.vertices = new Vertex[tokens.length];
            face.textureCoordinates = new TextureCoordinate[tokens.length];
            face.vertexNormals = new Vertex[tokens.length];

            // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ...
            if (line.matches(REGEX_FACE_VERTEX_TEXTURECOORD_VERTEXNORMAL))
            {
                for (int i = 0; i < tokens.length; ++i)
                {
                    subTokens = tokens[i].split("/");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                    face.vertexNormals[i] = vertexNormals.get(Integer.parseInt(subTokens[2]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1/vt1 v2/vt2 v3/vt3 ...
            else if (line.matches(REGEX_FACE_VERTEX_TEXTURECOORD))
            {
                for (int i = 0; i < tokens.length; ++i)
                {
                    subTokens = tokens[i].split("/");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1//vn1 v2//vn2 v3//vn3 ...
            else if (line.matches(REGEX_FACE_VERTEX_VERTEXNORMAL))
            {
                for (int i = 0; i < tokens.length; ++i)
                {
                    subTokens = tokens[i].split("//");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.vertexNormals[i] = vertexNormals.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1 v2 v3 ...
            else if (line.matches(REGEX_FACE_VERTEX))
            {
                for (int i = 0; i < tokens.length; ++i)
                {
                    face.vertices[i] = vertices.get(Integer.parseInt(tokens[i]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            else
                throw new IllegalArgumentException();
        }

        return face;
    }

    private GroupObject parseGroupObject(String line)
    {
        GroupObject group = null;

        if (isValidGroupObjectLine(line))
        {
            String trimmedLine = line.substring(line.indexOf(" ") + 1);

            if (trimmedLine.length() > 0)
            {
                group = new GroupObject(trimmedLine);
            }
        }

        return group;
    }

    private static boolean isValidVertexLine(String line)
    {

        return line.matches(REGEX_VERTEX);
    }

    private static boolean isValidVertexNormalLine(String line)
    {

        return line.matches(REGEX_VERTEX_NORMAL);
    }

    private static boolean isValidTextureCoordinateLine(String line)
    {

        return line.matches(REGEX_TEXTURE_COORDINATE);
    }

    private static boolean isValidFaceLine(String line)
    {

        return line.matches(REGEX_FACE_VERTEX_TEXTURECOORD_VERTEXNORMAL) || line.matches(REGEX_FACE_VERTEX_TEXTURECOORD) || line.matches(REGEX_FACE_VERTEX_VERTEXNORMAL) || line.matches(REGEX_FACE_VERTEX);
    }

    private static boolean isValidGroupObjectLine(String line)
    {

        return line.matches(REGEX_GROUP_OBJECT);
    }
}
