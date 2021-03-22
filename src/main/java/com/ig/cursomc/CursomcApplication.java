package com.ig.cursomc;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import com.ig.cursomc.domain.*;
import com.ig.cursomc.domain.enums.EstadoPagamento;
import com.ig.cursomc.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ig.cursomc.domain.enums.TipoCliente;

@SpringBootApplication
public class CursomcApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CursomcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}

}
