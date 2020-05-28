package com.infoshareacademy.service;

import com.infoshareacademy.domain.Drink;
import com.infoshareacademy.domain.Ingredient;
import com.infoshareacademy.repository.DrinkRepository;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class DrinkService {

    @EJB
    private DrinkRepository drinkRepository;

    public Drink findDrinkByName(Long drinkName) {

        return drinkRepository.findDrinkByName(drinkName);
    }

    public List<Drink> findDrinkByIngredients(List<Ingredient> ingredients) {

        return drinkRepository.findDrinkByIngredients(ingredients);
    }

/*    public List<Drink> findDrinkByName(String partialDrinkName) {

        if (partialDrinkName.length() < 2) {
            //pusta lista + brak resultatów albo error/wyjatek/za krótka nazwa

        }

        return drinkRepository.findAllDrinks().stream();
    }*/
}
