package br.com.banco.api.resources;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.banco.api.dto.ContaDTO;
import br.com.banco.api.model.entity.ApiMessage;
//import br.com.banco.api.model.repository.ContaRepository;
import br.com.banco.api.services.ContaService;
import br.com.banco.api.validacoes.ValidaCPF;

@RestController
@RequestMapping("/api/contas")
@CrossOrigin("*")
public class ContaResource {

	private Double limiteAbrirConta = 50.00;
	// private Double limiteMaximo = 500.00;

	@Autowired
	private ContaService contaService;

	@GetMapping
	public ResponseEntity<List<ContaDTO>> listarContas() {
		List<ContaDTO> lista = contaService.listarContas();
		return ResponseEntity.ok().body(lista);
	}

	@GetMapping("/{conta}")
	public ResponseEntity<List<ContaDTO>> listarPorNumeroConta(@PathVariable Integer conta) {
		List<ContaDTO> numeroConta = contaService.listarPorNumeroConta(conta);

		if (numeroConta.size() > 0) {
			return ResponseEntity.ok().body(numeroConta);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada");
		}
	}

	@PostMapping
	public ResponseEntity<Object> salvar(@Valid @RequestBody ContaDTO contaDTO) {

		try {
			if (contaDTO.getSaldo() < limiteAbrirConta) {
				return new ResponseEntity<>(new ApiMessage("Saldo insuficiente para abertura de nova conta."),
						HttpStatus.BAD_REQUEST);
			} else if ("".equals(contaDTO.getCpf()) || contaDTO.getCpf() == null) {
				return new ResponseEntity<>(new ApiMessage("É necessário informar um cpf para abertura de nova conta."),
						HttpStatus.BAD_REQUEST);
			} else if (ValidaCPF.isCPF(contaDTO.getCpf()) == false) {
				return new ResponseEntity<>(new ApiMessage("CPF informado para criação de conta está inválido."),
						HttpStatus.BAD_REQUEST);
			} else {

				if (contaDTO.getSaldo() != null) {
					ContaDTO newDTO = contaService.salvar(contaDTO);

					URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{conta}")
							.buildAndExpand(newDTO.getConta()).toUri();

					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.setLocation(location);
					responseHeaders.set("MyResponseHeader", "MyValue");

					return new ResponseEntity<>(
							new ApiMessage("Conta cadastrada com sucesso! Conta Nº " + newDTO.getConta()),
							responseHeaders, HttpStatus.CREATED);
				} else {
					return new ResponseEntity<>(new ApiMessage("Informar o valor de abertura"), HttpStatus.BAD_REQUEST);
				}
			}

		} catch (NullPointerException e) {
			return new ResponseEntity<>(new ApiMessage("O campo valor abertura não pode ser vazio"),
					HttpStatus.BAD_REQUEST);
		}

	}

	@PutMapping("/depositar/{conta}")
	public ResponseEntity<String> depositar(@Valid @RequestBody ContaDTO contaDTO, @PathVariable Integer conta) {

		Optional<List<ContaDTO>> numeroConta = Optional.of(contaService.listarPorNumeroConta(conta));

		if (numeroConta.isPresent()) {
			contaService.depositar(contaDTO, conta);
		} else {
			return ResponseEntity.badRequest().body("Não foi possível realizar o depósito");
		}
		return ResponseEntity.ok().body("Depósito realizado com sucesso!");
	}

	@PutMapping("/sacar/{conta}")
	public ResponseEntity<String> sacar(@Valid @RequestBody ContaDTO contaDTO, @PathVariable Integer conta) {
		Optional<List<ContaDTO>> numeroConta = Optional.of(contaService.listarPorNumeroConta(conta));
		if (numeroConta.isPresent()) {
			contaService.sacar(contaDTO, conta);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível realizar o saque");
		}
		return ResponseEntity.ok().body("Saque realizado com sucesso!");
	}

	@PutMapping("/transferir/{conta}/beneficiario/{conta2}")
	public ResponseEntity<String> transferir(@Valid @RequestBody ContaDTO contaDTO, @PathVariable Integer conta,
			@PathVariable Integer conta2) {
		Optional<List<ContaDTO>> numeroConta = Optional.of(contaService.listarPorNumeroConta(conta));
		Optional<List<ContaDTO>> numeroConta2 = Optional.of(contaService.listarPorNumeroConta(conta2));

		if (numeroConta.isPresent() && numeroConta2.isPresent()) {
			contaService.transferir(contaDTO, conta, conta2);
		}
		return ResponseEntity.ok().body("Transferência realizada com sucesso!");
	}

}
