package com.ic.er.util;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Records;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.attribute.Records.*;
import static guru.nidi.graphviz.model.Compass.*;

public class DrawingUtil {

    public static BufferedImage drawingERModel() {
        Node node0 = Factory.node("node0").with(Records.of(rec("f0", ""), rec("f1", ""), rec("f2", ""), rec("f3", ""), rec("f4", "")));
        Node node1 = Factory.node("node1").with(Records.of(turn(rec("n4"), rec("v", "719"), rec(""))));
        Node node2 = Factory.node("node2").with(Records.of(turn(rec("a1"), rec("805"), rec("p", ""))));
        Node node3 = Factory.node("node3").with(Records.of(turn(rec("i9"), rec("718"), rec(""))));
        Node node4 = Factory.node("node4").with(Records.of(turn(rec("e5"), rec("989"), rec("p", ""))));
        Node node5 = Factory.node("node5").with(Records.of(turn(rec("t2"), rec("v", "959"), rec(""))));
        Node node6 = Factory.node("node6").with(Records.of(turn(rec("o1"), rec("794"), rec(""))));
        Node node7 = Factory.node("node7").with(Records.of(turn(rec("s7"), rec("659"), rec(""))));
        Graph g = Factory.graph("example3").directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .with(
                        node0.link(
                                Link.between(Factory.port("f0"), node1.port("v", SOUTH)),
                                Link.between(Factory.port("f1"), node2.port(WEST)),
                                Link.between(Factory.port("f2"), node3.port(WEST)),
                                Link.between(Factory.port("f3"), node4.port(WEST)),
                                Link.between(Factory.port("f4"), node5.port("v", NORTH))),
                        node2.link(Link.between(Factory.port("p"), node6.port(NORTH_WEST))),
                        node4.link(Link.between(Factory.port("p"), node7.port(SOUTH_WEST))));
        BufferedImage image = Graphviz.fromGraph(g).width(900).render(Format.PNG).toImage();


        return image;
    }

    public static void main(String[] args) throws IOException {
        BufferedImage image = drawingERModel();
        File outputfile = new File("D:\\study\\master\\group project\\AmazingProject\\src\\main\\java\\com\\ic\\er\\image.jpg");
        ImageIO.write(image, "jpg", outputfile);
    }
}
