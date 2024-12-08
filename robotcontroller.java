package com.igus.controller;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RobotController {
    private static final String CONFIG_FILE = "C:\\Users\\DELL\\Downloads\\rebel4dof_config.xml";
    private Map<Integer, Joint> joints = new HashMap<>();
    private Document doc;

    // Class to store joint details
    class Joint {
        int id;
        int a1, a2, a3, a4; // Angles for each joint (A1, A2, A3, A4)
        Element jointElement; // Store reference to the XML element

        public Joint(int id, int a1, int a2, int a3, int a4, Element jointElement) {
            this.id = id;
            this.a1 = a1;
            this.a2 = a2;
            this.a3 = a3;
            this.a4 = a4;
            this.jointElement = jointElement;
        }

        public void moveTo(int newA1, int newA2, int newA3, int newA4) {
            // Update the angles for the joint
            a1 = newA1;
            a2 = newA2;
            a3 = newA3;
            a4 = newA4;

            // Update the XML element's attributes for the angles
            jointElement.setAttribute("a1", String.valueOf(a1));
            jointElement.setAttribute("a2", String.valueOf(a2));
            jointElement.setAttribute("a3", String.valueOf(a3));
            jointElement.setAttribute("a4", String.valueOf(a4));

            System.out.println("Joint " + id + " moved to: A1=" + a1 + ", A2=" + a2 + ", A3=" + a3 + ", A4=" + a4);
        }
    }

    // Method to load and parse the XML file
    public void loadRobotConfig() {
        try {
            File xmlFile = new File(CONFIG_FILE);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList jointList = doc.getElementsByTagName("Joint");

            for (int i = 0; i < jointList.getLength(); i++) {
                Node jointNode = jointList.item(i);

                if (jointNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element jointElement = (Element) jointNode;

                    int id = Integer.parseInt(jointElement.getAttribute("Nr"));
                    int a1 = Integer.parseInt(jointElement.getAttribute("a1"));
                    int a2 = Integer.parseInt(jointElement.getAttribute("a2"));
                    int a3 = Integer.parseInt(jointElement.getAttribute("a3"));
                    int a4 = Integer.parseInt(jointElement.getAttribute("a4"));

                    // Create a Joint object for each joint in the XML
                    Joint joint = new Joint(id, a1, a2, a3, a4, jointElement);
                    joints.put(id, joint);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to move a joint to new angles
    public void moveJoint(int jointId, int a1, int a2, int a3, int a4) {
        Joint joint = joints.get(jointId);
        if (joint != null) {
            joint.moveTo(a1, a2, a3, a4);
        } else {
            System.out.println("Error: Joint " + jointId + " not found.");
        }
    }

    // Method to save updated XML file
    public void saveConfig() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(CONFIG_FILE));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Pretty print
            transformer.transform(source, result);
            System.out.println("Updated configuration saved to XML file.");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RobotController controller = new RobotController();
        controller.loadRobotConfig();

        // Example movements
        controller.moveJoint(1, 10, 20, 30, 40); // Move Joint 1 to A1=10, A2=20, A3=30, A4=40
        controller.moveJoint(2, 20, 35, 35, 45); // Move Joint 2 to A1=15, A2=25, A3=35, A4=45
        controller.moveJoint(3, 30, 50, 25, 79); // Move Joint 3 to A1=30, A2=-50, A3=25, A4=0
        controller.moveJoint(4, 30, 50, 25, 90); // Move Joint 4 to A1=30, A2=-50, A3=25, A4=90

        // Save the updated configuration to XML
        controller.saveConfig();
    }
}
