package com.mycompany.controlvotaciones;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

public class BDXML {

    private static final String RUTA = "registros.xml";

    // =====================================================
    // DOCUMENTO XML
    // =====================================================
    public static Document obtenerDocumento() {
        try {
            File archivo = new File(RUTA);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (!archivo.exists()) {
                Document doc = builder.newDocument();
                Element raiz = doc.createElement("registros");
                doc.appendChild(raiz);
                raiz.appendChild(doc.createElement("candidatos"));
                guardarDocumento(doc);
                return doc;
            }

            Document doc = builder.parse(archivo);
            doc.getDocumentElement().normalize();
            return doc;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al cargar el XML:\n" + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void guardarDocumento(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(RUTA));
            transformer.transform(source, result);
        } catch (Exception e) {
            System.err.println("Error guardando XML: " + e.getMessage());
        }
    }

    // =====================================================
    // REGISTRAR CANDIDATO
    // =====================================================
    public static boolean registrarCandidato(Candidato c) {
        try {
            Document doc = obtenerDocumento();
            if (doc == null) return false;

            if (existeCandidato(c.getFullName())) {
                JOptionPane.showMessageDialog(null, "Este candidato ya está registrado.");
                return false;
            }

            Element candidatosRaiz = (Element) doc.getElementsByTagName("candidatos").item(0);

            int nuevoId = obtenerSiguienteId(doc);

            Element candidatoElem = doc.createElement("candidato");
            candidatoElem.setAttribute("id", String.valueOf(nuevoId));

            agregarElemento(doc, candidatoElem, "fullName", c.getFullName());
            agregarElemento(doc, candidatoElem, "grade", c.getGrade());
            agregarElemento(doc, candidatoElem, "gender", c.getGender());
            agregarElemento(doc, candidatoElem, "votos", "0");

            candidatosRaiz.appendChild(candidatoElem);
            guardarDocumento(doc);

            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar:\n" + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // ELIMINAR CANDIDATO
    // =====================================================
    public static boolean eliminarCandidato(String fullName) {
        try {
            Document doc = obtenerDocumento();
            if (doc == null) return false;

            NodeList lista = doc.getElementsByTagName("candidato");
            for (int i = 0; i < lista.getLength(); i++) {
                Element cand = (Element) lista.item(i);
                String nombreXML = cand.getElementsByTagName("fullName").item(0).getTextContent();

                if (nombreXML.equalsIgnoreCase(fullName)) {
                    cand.getParentNode().removeChild(cand);
                    guardarDocumento(doc);
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar:\n" + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // UTILIDADES
    // =====================================================
    private static int obtenerSiguienteId(Document doc) {
        NodeList lista = doc.getElementsByTagName("candidato");
        int max = 0;
        for (int i = 0; i < lista.getLength(); i++) {
            Element elem = (Element) lista.item(i);
            try {
                int id = Integer.parseInt(elem.getAttribute("id"));
                if (id > max) max = id;
            } catch (Exception ignored) {}
        }
        return max + 1;
    }

    private static void agregarElemento(Document doc, Element padre, String tag, String valor) {
        Element e = doc.createElement(tag);
        e.setTextContent(valor);
        padre.appendChild(e);
    }

    public static List<Candidato> obtenerTodosCandidatos() {
        List<Candidato> lista = new ArrayList<>();
        try {
            Document doc = obtenerDocumento();
            if (doc == null) return lista;

            NodeList nodes = doc.getElementsByTagName("candidato");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element elem = (Element) nodes.item(i);
                int id = Integer.parseInt(elem.getAttribute("id"));
                String name = elem.getElementsByTagName("fullName").item(0).getTextContent();
                String grade = elem.getElementsByTagName("grade").item(0).getTextContent();
                String gender = elem.getElementsByTagName("gender").item(0).getTextContent();
                int votos = Integer.parseInt(elem.getElementsByTagName("votos").item(0).getTextContent());

                lista.add(new Candidato(id, name, grade, gender, votos));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static boolean existeCandidato(String fullName) {
        for (Candidato c : obtenerTodosCandidatos()) {
            if (c.getFullName().equalsIgnoreCase(fullName)) {
                return true;
            }
        }
        return false;
    }
    
        // =====================================================
    // REGISTRAR VOTOS
    // =====================================================
    public static boolean registrarVotos(String nombreCandidato, int cantidadVotos) {
        try {
            Document doc = obtenerDocumento();
            if (doc == null) return false;

            NodeList lista = doc.getElementsByTagName("candidato");

            for (int i = 0; i < lista.getLength(); i++) {
                Element cand = (Element) lista.item(i);
                String nombreXML = cand.getElementsByTagName("fullName").item(0).getTextContent();

                if (nombreXML.equalsIgnoreCase(nombreCandidato)) {
                    Element votosElem = (Element) cand.getElementsByTagName("votos").item(0);
                    int votosActuales = Integer.parseInt(votosElem.getTextContent());
                    votosElem.setTextContent(String.valueOf(votosActuales + cantidadVotos));

                    guardarDocumento(doc);
                    return true;
                }
            }
            return false; // Candidato no encontrado

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar votos:\n" + e.getMessage());
            return false;
        }
    }
    
}