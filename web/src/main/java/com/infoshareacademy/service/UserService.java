package com.infoshareacademy.service;

import com.infoshareacademy.domain.Drink;
import com.infoshareacademy.domain.User;
import com.infoshareacademy.domain.dto.FullDrinkView;
import com.infoshareacademy.repository.DrinkRepository;
import com.infoshareacademy.repository.UserRepository;
import com.infoshareacademy.service.mapper.FullDrinkMapper;
import com.infoshareacademy.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;


@Stateless
public class UserService {

    private static final Logger packageLogger = LoggerFactory.getLogger(UserService.class.getName());

    @EJB
    private UserRepository userRepository;

    @EJB
    private DrinkRepository drinkRepository;

    @Inject
    private FullDrinkMapper fullDrinkMapper;


    public void saveOrDeleteFavourite(String userId, String drinkId) {

        Drink drink = drinkRepository.findDrinkById(Long.parseLong(drinkId));
        User user = userRepository.findUserById(Long.parseLong(userId));

        List<Drink> favouriteDrinks = user.getDrinks();

        if (!favouriteDrinks.contains(drink)) {
            user.getDrinks().add(drink);
            packageLogger.info("User ID = {} added drink ID = {} to favourites", userId, drinkId);

        } else {
            user.getDrinks().remove(drink);
            packageLogger.info("User ID = {} deleted drink ID = {} from favourites", userId, drinkId);

        }

    }

    public List<FullDrinkView> favouritesList(String userId) {

        User user = userRepository.findUserById(Long.parseLong(userId));

        return fullDrinkMapper.toView(user.getDrinks());

    }

}
