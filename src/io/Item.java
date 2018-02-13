package io;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.Queue;

public class Item implements Itemable {
    private static final double DEFAULT_FADE = 1;

    private static final int EDGE_IGNORE = 0;
    private static final int EDGE_BOUNCE = 1;
    private static final int EDGE_STOP = 2;
    private static final int EDGE_REMOVE = 3;

    private static final double DEFAULT_SPEED = calculateSpeed(30);

    private Node node;
    private double dx, dy, dr, da; //deltas x, y, rotate, alpha
    private double dir, speed;
    private EventHandler<Event> edgeHandler;
    private int edge = EDGE_IGNORE;
    private Queue<EventHandler> queue;
    private int waiting = 0;

    public Item(Node node) {
        this.node = node;

        queue = new LinkedList<>();
    }

    public String getId() {
        return node.getId();
    }

    public String getType() {
        return node.getClass().getName();
    }

    void tick() {
        waiting--;

        boolean circle = node instanceof Circle;
        boolean rect = node instanceof Rectangle;
        boolean text = node instanceof Text;

        while (waiting <= 0 && queue.size() > 0) {
            queue.remove().handle(new Event(node,Event.NULL_SOURCE_TARGET,Event.ANY));
        }

        if (dx != 0) {
            if (circle) ((Circle)node).setCenterX(((Circle)node).getCenterX() + dx);
            else node.setLayoutX(node.getLayoutX() + dx);
        }

        if (dy != 0) {
            if (circle) ((Circle)node).setCenterY(((Circle)node).getCenterY() + dy);
            else node.setLayoutY(node.getLayoutY() + dy);
        }

       // System.out.println(node.getClass().getName() + "|" + edge);

        if (edge != EDGE_IGNORE && (dx != 0 || dy != 0)) {

            Bounds screen = Window.edges();
            //double w = Window.width();

            double w = node.getParent().getBoundsInParent().getWidth();
            double h = node.getParent().getBoundsInParent().getHeight();

            Bounds bounds = node.getBoundsInParent();


            double x = bounds.getMaxX();

            if (node instanceof Circle) {
               // System.out.println(node.getClass().getName() + "|" + bounds.getMinY() + "|" + bounds.getMaxY() + "|" + node.getLayoutY()+ "|" + ((Circle)node).getCenterY());
            }

            if (bounds.getMinX() < 0) {
                if (edge == EDGE_BOUNCE) {
                    direction(180 - dir);
                    if (circle) {
                        ((Circle)node).setCenterX(((Circle)node).getRadius());
                    } else {
                        node.setLayoutX(0);
                    }
                } else if (edge == EDGE_STOP) {

                    if (circle) {
                        ((Circle)node).setCenterX(((Circle)node).getRadius());
                    } else {
                        node.setLayoutX(0);
                    }

                    speed(0);
                } else if (edge == EDGE_REMOVE) {
                    this.remove();
                }
            } else if (x > w) {
                if (edge == EDGE_BOUNCE) {
                    direction(180 - dir);
                    if (circle) {
                        ((Circle)node).setCenterX(w - ((Circle)node).getRadius());
                    } else {
                        node.setLayoutX(w - node.getLayoutBounds().getWidth());
                    }
                } else if (edge == EDGE_STOP) {
                    if (circle) {
                        ((Circle)node).setCenterX(w - ((Circle)node).getRadius());
                    } else {
                        node.setLayoutX(w - node.getLayoutBounds().getWidth());
                    }
                    speed(0);
                } else if (edge == EDGE_REMOVE) {
                    this.remove();
                }
            }

            if (bounds.getMinY() < 0) {
                if (edge == EDGE_BOUNCE) {
                    direction(360 - dir);
                    if (circle) {
                        ((Circle)node).setCenterY(((Circle)node).getRadius());
                    } else {
                        node.setLayoutY(0);
                    }
                } else if (edge == EDGE_STOP) {
                    if (circle) {
                        ((Circle)node).setCenterY(((Circle)node).getRadius());
                    } else {
                        node.setLayoutY(0);
                    }
                    speed(0);
                } else if (edge == EDGE_REMOVE) {
                    this.remove();
                }
            }

            if (bounds.getMaxY() > h) {
                if (edge == EDGE_BOUNCE) {
                    direction(360 - dir);


                    if (circle) {
                       // System.out.println(h - node.getLayoutBounds().getHeight());
                        ((Circle)node).setCenterY(h - ((Circle)node).getRadius());
                    } else if (text) {
                        int pixels = 1;

                        while (bounds.getMaxY() > h + pixels) pixels++;

                        node.setLayoutY(h - pixels);
                    } else {
                        node.setLayoutY(h - node.getLayoutBounds().getHeight());
                    }
                } else if (edge == EDGE_STOP) {
                    if (circle) {
                        // System.out.println(h - node.getLayoutBounds().getHeight());
                        ((Circle)node).setCenterY(h - ((Circle)node).getRadius());
                    } else {
                        node.setLayoutY(h - node.getLayoutBounds().getHeight());
                    }
                    speed(0);
                } else if (edge == EDGE_REMOVE) {
                    this.remove();
                }
            }
        }

        if (dr != 0) {
            double r = (node.getRotate() + dr) % 360;
            node.setRotate(r);
        }

        if (node.getOpacity() + da > 1) {
            node.setOpacity(1);
        } else if (node.getOpacity() + da < 0) {
            node.setOpacity(0);
        } else {
            node.setOpacity(node.getOpacity() + da);
        }
    }


    public Itemable onOver(EventHandler<MouseEvent> handler) {
        if (waiting > 0) {
            queue.add(e->onOver(handler));
        } else {
            node.setOnMouseEntered(handler);
        }
        return this;
    }

    public Itemable onOut(EventHandler<MouseEvent> handler) {
        if (waiting > 0) {
            queue.add(e->onOut(handler));
        } else {
            node.setOnMouseExited(handler);
        }
        return this;
    }

    public Itemable onClick(EventHandler<MouseEvent> handler) {
        if (waiting > 0) {
            queue.add(e->onClick(handler));
        } else {
            node.setOnMousePressed(handler);
        }
        return this;
    }

    @Override
    public Itemable onEdge(EventHandler<Event> handler) {
        if (waiting > 0) {
            queue.add(e-> onEdge(handler));
        } else {
            edgeHandler = handler;
        }
        return this;
    }

    public Itemable stroke(Color color) {
        if (waiting > 0) {
            queue.add(e->stroke(color));
        } else {

            if (node instanceof Shape) {
                ((Shape) node).setStroke(color);
            }
        }

        return this;
    }

    public Itemable fill(Color color) {
        // System.out.println(fill);
        if (waiting > 0) {
            queue.add(e->fill(color));
        } else {
            if (node instanceof Shape) {
                ((Shape) node).setFill(color);
            }
        }

        return this;
    }

    private void updateDeltas() {
        dx = Math.cos(Math.toRadians(dir)) * speed;
        dy = Math.sin(Math.toRadians(dir)) * speed;
       // System.out.println("dx="+dx+",dy="+dy);
    }

    private static double calculateSpeed(double pps) {
        return pps / Window.FPS;
    }

    public Itemable direction(double angle, double pps) {
        if (waiting > 0) {
            queue.add(e->direction(angle, pps));
        } else {
            speed = pps;
            direction(angle);
        }
        return this;
    }

    public Itemable direction(double angle) {
        if (waiting > 0) {
            queue.add(e->direction(angle));
        } else {
            dir = angle;

            while (dir < 0) dir += 360;

            dir = dir % 360;

            updateDeltas();
        }

        return this;
    }

    @Override
    public Itemable randomDirection() {
        direction(Math.random() * 360);
        return this;
    }

    public Itemable speed(double pps) {
        if (waiting > 0) {
            queue.add(e->speed(pps));
        } else {
            speed = calculateSpeed(pps);
            updateDeltas();
        }
        return this;
    }

    public Itemable left(double pps) {
        if (waiting > 0) {
            queue.add(e->left(pps));
        } else {
            this.speed = calculateSpeed(pps);
            direction(180);
        }
        return this;
    }

    public Itemable left() {
        if (waiting > 0) {
            queue.add(e->left());
        } else {
            if (speed <= 0) {
                speed = DEFAULT_SPEED;
            }
            direction(180);
        }
        return this;
    }

    public Itemable right(double pps) {
        if (waiting > 0) {
            queue.add(e->right(pps));
        } else {
            this.speed = calculateSpeed(pps);
            direction(0);
        }
        return this;
    }

    public Itemable right() {
        if (waiting > 0) {
            queue.add(e->right());
        } else {
            if (speed <= 0) {
                speed = DEFAULT_SPEED;
            }
            direction(0);
        }
        return this;
    }

    @Override
    public Itemable up(double pps) {
        if (waiting > 0) {
            queue.add(e->up(pps));
        } else {
            this.speed = calculateSpeed(pps);
            direction(270);
        }
        return this;
    }

    @Override
    public Itemable up() {
        if (waiting > 0) {
            queue.add(e->up());
        } else {
            if (speed <= 0) {
                speed = DEFAULT_SPEED;
            }
            direction(270);
        }
        return this;
    }

    @Override
    public Itemable down(double pps) {
        if (waiting > 0) {
            queue.add(e->down(pps));
        } else {
            this.speed = calculateSpeed(pps);
            direction(90);
        }
        return this;
    }

    @Override
    public Itemable down() {
        if (waiting > 0) {
            queue.add(e->down());
        } else {
            if (speed <= 0) {
                speed = DEFAULT_SPEED;
            }
            direction(90);
        }
        return this;
    }

    @Override
    public Itemable hide() {
        if (waiting > 0) {
            queue.add(e->hide());
        } else {
            node.setVisible(false);
        }
        return this;
    }

    @Override
    public Itemable show() {
        if (waiting > 0) {
            queue.add(e->show());
        } else {
            node.setVisible(true);
        }
        return this;
    }

    @Override
    public Itemable bounceHorizontal() {
        if (waiting > 0) {
            queue.add(e-> bounceHorizontal());
        } else {
            direction(360 - dir);
        }
        return this;
    }

    @Override
    public Itemable bounceVertical() {
        if (waiting > 0) {
            queue.add(e-> bounceVertical());
        } else {
            direction(180 - dir);
        }
        return this;
    }


    public Itemable rotate(double degrees) {
        if (waiting > 0) {
            queue.add(e->rotate(degrees));
        } else {
            dr = degrees;
        }
        return this;
    }

    @Override
    public Itemable fadeIn() {
        if (waiting > 0) {
            queue.add(e->fadeIn());
        } else {
            node.setVisible(true);
            node.setOpacity(0);
            da = 1 / (Window.FPS * DEFAULT_FADE);
            sleep(DEFAULT_FADE);
        }
        return this;
    }

    @Override
    public Itemable fadeOut() {
        if (waiting > 0) {
            queue.add(e->fadeOut());
        } else {
            da = -1 / (Window.FPS * DEFAULT_FADE);
            sleep(DEFAULT_FADE);
        }

        return this;
    }


    @Override
    public Itemable fadeIn(double seconds) {
        if (waiting > 0) {
            queue.add(e->fadeIn(seconds));
        } else {
            node.setOpacity(0);
            da = 1 / (Window.FPS * seconds);
            sleep(seconds);
        }
        return this;
    }

    @Override
    public Itemable fadeOut(double seconds) {
        if (waiting > 0) {
            queue.add(e->fadeOut(seconds));
        } else {
            node.setOpacity(1);
            da = -1 / (Window.FPS * seconds);
            sleep(seconds);
        }

        return this;
    }

    @Override
    public Itemable sleep(double seconds) {
        if (waiting > 0) {
            queue.add(e->sleep(seconds));
        } else {
            waiting = (int) Math.round(seconds * Window.FPS);
        }

        return this;
    }

    @Override
    public void remove() {
        if (waiting > 0) {
            queue.add(e->remove());
        } else {
            Window.remove(this);
        }

    }

    Node getNode() {
        return node;
    }


    @Override
    public Itemable edgeBounce() {
        if (waiting > 0) {
            queue.add(e-> edgeBounce());
        } else {
            edge = EDGE_BOUNCE;
        }
        return this;
    }

    @Override
    public Itemable edgeStop() {
        if (waiting > 0) {
            queue.add(e-> edgeStop());
        } else {
            edge = EDGE_STOP;
        }
        return this;
    }

    @Override
    public Itemable edgeRemove() {
        if (waiting > 0) {
            queue.add(e-> edgeRemove());
        } else {
            edge = EDGE_REMOVE;
        }
        return this;
    }

    @Override
    public Itemable edgeIgnore() {
        if (waiting > 0) {
            queue.add(e-> edgeRemove());
        } else {
            edge = EDGE_IGNORE;
        }
        return this;
    }



}