package org.traxnet.shadingzen.simulation;

import org.traxnet.shadingzen.core.Action;

/**
 * Represents a finite action that is run by a BehaviourState as part of its current goal.
 * For example a hungry cat (HungryState) to satisfy it current goal of becoming not hungry first
 * needs to run an ApproachAction (to a pizza) and ten an EatFoodAction. This allows reusing
 * the ApproachAction for another BehaviourSte.
 */
public abstract class BehaviourAction extends Action {

}
