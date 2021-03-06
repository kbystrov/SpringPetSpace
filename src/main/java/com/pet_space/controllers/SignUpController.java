package com.pet_space.controllers;

import com.pet_space.models.essences.RoleEssence;
import com.pet_space.models.essences.StatusEssence;
import com.pet_space.models.essences.UserEssence;
import com.pet_space.repositories.UserEssenceRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.pet_space.models.essences.RoleEssence.RoleEssenceEnum.USER;
import static com.pet_space.models.essences.StatusEssence.StatusEssenceEnum.ACTIVE;
import static com.pet_space.models.essences.StatusEssence.StatusEssenceEnum.INACTIVE;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class SignUpController {
    private static final Logger LOG = getLogger(SignUpController.class);
    private final UserEssenceRepository userEssenceRepository;

    @Autowired
    public SignUpController(UserEssenceRepository userEssenceRepository) {
        this.userEssenceRepository = userEssenceRepository;
    }

    @RequestMapping(value = "sign_up", method = RequestMethod.POST)
    public String postUserEssence(@ModelAttribute("userEssence") @Valid UserEssence userEssence, BindingResult result, RedirectAttributes redirectAttributes) {
        boolean existsByNickname = this.userEssenceRepository.existsByNickname(userEssence.getNickname());
        boolean existsByEmail = this.userEssenceRepository.existsByEmail(userEssence.getEmail());
        if (!result.hasErrors() && !existsByNickname && !existsByEmail) {
            if (userEssence.getRole().equals(RoleEssence.of(USER)))
                userEssence.setStatusEssence(StatusEssence.of(ACTIVE));
            else userEssence.setStatusEssence(StatusEssence.of(INACTIVE));

            this.userEssenceRepository.save(userEssence);
            redirectAttributes.addFlashAttribute("stateRegistration", Boolean.TRUE);
        } else {
            List<ImmutablePair> errors = result.getFieldErrors().stream()
                    .map(e -> ImmutablePair.of(StringUtils.capitalize(e.getField()), e.getDefaultMessage()))
                    .collect(Collectors.toList());

            if (existsByNickname) errors.add(ImmutablePair.of("Nickname", "This nickname already used"));
            if (existsByEmail) errors.add(ImmutablePair.of("Email", "This email already used"));

            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("stateRegistration", Boolean.FALSE);
        }
        return "redirect:/";
    }
}
