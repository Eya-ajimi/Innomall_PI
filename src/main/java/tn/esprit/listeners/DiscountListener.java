package tn.esprit.listeners;

import tn.esprit.events.DiscountEvent;

public interface DiscountListener {
    void onDiscountCreated(DiscountEvent event);
}