package com.ig.cursomc.services;

import com.ig.cursomc.domain.Cidade;
import com.ig.cursomc.domain.Cliente;
import com.ig.cursomc.domain.Endereco;
import com.ig.cursomc.domain.enums.Perfil;
import com.ig.cursomc.domain.enums.TipoCliente;
import com.ig.cursomc.dto.ClienteDTO;
import com.ig.cursomc.dto.ClienteNewDTO;
import com.ig.cursomc.repositories.CidadeRepository;
import com.ig.cursomc.repositories.ClienteRepository;
import com.ig.cursomc.repositories.EnderecoRepository;
import com.ig.cursomc.resources.exception.AuthorizationException;
import com.ig.cursomc.security.UserSS;
import com.ig.cursomc.services.exceptions.DataIntegrityException;
import com.ig.cursomc.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private BCryptPasswordEncoder pe;


	public Cliente find(Integer id) {

		UserSS user = UserService.authenticated();
		if (user==null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}

		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
		"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
		}

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas");
		}
	}

	public List<Cliente> findAll() {

		return repo.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(
				objDto.getId(),
				objDto.getNome(),
				objDto.getEmail(),
				null,
				null,
				null);
	}

	public Cliente fromDTO(ClienteNewDTO objDto) {

		Cliente cli = new Cliente(
				null,
				objDto.getNome(),
				objDto.getEmail(),
				objDto.getCpfOuCnpj(),
				TipoCliente.toEnum(objDto.getTipo()),
				pe.encode(objDto.getSenha()));

		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);

		Endereco end = new Endereco(
				null,
				objDto.getLogradouro(),
				objDto.getNumero(),
				objDto.getComplemento(),
				objDto.getBairro(),
				objDto.getCep(),
				cli,
				cid);

		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		if (objDto.getTelefone2() != null) {
			cli.getTelefones().add(objDto.getTelefone2());
		}
		if (objDto.getTelefone3() != null) {
			cli.getTelefones().add(objDto.getTelefone3());
		}
		return cli;
	}


	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

}
